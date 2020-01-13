package com.ghostflow.services;

import com.ghostflow.database.BidRepository;
import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.http.security.GhostFlowAccessDeniedException;
import com.ghostflow.utils.TypeStatePair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.ghostflow.database.postgres.entities.UserEntity.WAITING_FOR_TO_ROLES;
import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service
public class LongPollingBidServiceImpl implements LongPollingBidService {

    private static final int TIMEOUT_SECONDS;

    static {
        TIMEOUT_SECONDS = 100;
    }

    private final UserService userService;
    private final BidRepository bidRepository;

    private final Map<UserEntity.Role, RoleLock> locks = new HashMap<>();


    @Autowired
    public LongPollingBidServiceImpl(UserService userService, BidRepository bidRepository) {
        this.userService = userService;
        this.bidRepository = bidRepository;
        for (UserEntity.Role role : UserEntity.Role.values()) {
            if (!role.getWaitingFor().isEmpty()) {
                RoleLock lock = new RoleLock();
                locks.put(role, lock);
            }
        }
    }

    @Override
    public Bids<?> getCreatedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByCustomerExtended(user.getUserId(), limit, offset);
    }


    @Override
    public Bids<?> getAcceptedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByEmployeeExtended(user.getUserId(), limit, offset);
    }

    @Override
    public BidEntity<?> getBid(String email, long id) {
        UserEntity user = userService.get(email);
        ExtendedBidEntity<?> bid = bidRepository.extended().findExtended(id).orElseThrow(() -> new IllegalArgumentException("unknown bid id"));

        if(Objects.equals(user.getUserId(), bid.getCustomerId())
            || Objects.equals(user.getUserId(), bid.getEmployeeId())
            || user.getRole().getWaitingFor().stream().anyMatch(r -> bid.getType() == r.getType() || bid.getState() == r.getState())) {
            return bid;
        } else {
            throw new GhostFlowAccessDeniedException();
        }
    }

    @Override
    public Bids<?> getBidsByRole(String email, BidEntity.Type typeFilter, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findExtended(limit, offset, getTypesStatesByRole(user.getRole(), typeFilter));
    }

    @Override
    public Bids<?> waitForNewBidsByRole(String email, Long lastUpdateTime) {
        if (lastUpdateTime == null) {
            return new Bids<>((Long) null, Instant.now().toEpochMilli());
        }
        UserEntity user = userService.get(email);
        String[] typesStates = getTypesStatesByRole(user.getRole(), null);
        RoleLock lock = locks.get(user.getRole());
        Semaphore semaphore = lock.getSemaphore();
        boolean acquired = false;
        try {
            if (lastUpdateTime >= lock.getLastUpdateTime()) {
                acquired = semaphore.tryAcquire(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    return new Bids<>((Long) null, lastUpdateTime);
                }
            }
        } catch (InterruptedException e) {
            log.error("TryLock interrupted", e);
            return new Bids<>((Long) null, lastUpdateTime);
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
        return bidRepository.extended().findGtThanUpdateTimeExtended(lastUpdateTime, typesStates);
    }

    private static final String[] getTypesStatesByRole(UserEntity.Role role, BidEntity.Type type) {
        String[] typesStates = UserEntity.Role.waitingForToStr(role.getWaitingFor().stream().filter(tsp -> type == null || type == tsp.getType()));
        checkArgument(typesStates.length > 0, "Unable to get bids for role ");
        return typesStates;
    }

    @Override
    public ExtendedBidEntity<?> createBid(String email, BidEntity.Description description) {
        UserEntity user = userService.get(email);
        BidEntity.Type type = BidEntity.Type.fromClass.get(description.getClass());
        switch (type) {
            case REPAIR: {
                break;
            }
            case COMMON: {
                checkArgument(user.getRole() == UserEntity.Role.CLIENT, new GhostFlowAccessDeniedException());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown description type");
            }
        }
        TypeStatePair key = new TypeStatePair(type, BidEntity.State.PENDING);

        long id = updateAndNotify(updateTime -> {
            BidEntity<?> bid = new BidEntity<>(null, user.getUserId(), null, BidEntity.State.PENDING, updateTime, description, null);
            return bidRepository.create(bid);
        }, key);
        return bidRepository.extended().findExtended(id).get();
    }

    @Override
    public ExtendedBidEntity<?> updateBid(String email, long bidId, BidEntity.Description description, Action action) {
        UserEntity user = userService.get(email);

        List<Semaphore> oldSemaphores = new ArrayList<>();
        bidRepository.updateSafely(bidId, oldValue -> {
            if (action == Action.UPDATE) {
                checkArgument(Objects.equals(oldValue.getEmployeeId(), user.getUserId())
                    || oldValue.getState() == BidEntity.State.PENDING && Objects.equals(oldValue.getCustomerId(), user.getUserId()), new GhostFlowAccessDeniedException());
                return new BidEntity<>(
                    oldValue.getBidId(),
                    oldValue.getCustomerId(),
                    oldValue.getEmployeeId(),
                    oldValue.getState(),
                    oldValue.getUpdateTime(),
                    oldValue.getDescription(),
                    null
                );
            }
            checkArgument(oldValue.getEmployeeId() == null || Objects.equals(oldValue.getEmployeeId(), user.getUserId()), new GhostFlowAccessDeniedException());
            UserEntity.Role roleRequired;
            BidEntity.State newState;
            BidEntity.Type type;
            Long newEmployeeId;
            BidEntity.Description newDescription = description;

            if (oldValue.getDescription() instanceof BidEntity.CommonDescription) {

                type = BidEntity.Type.COMMON;
                switch (oldValue.getState()) {
                    case PENDING: {
                        roleRequired = UserEntity.Role.INVESTIGATION;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED;
                        break;
                    }
                    case ACCEPTED: {
                        roleRequired = UserEntity.Role.INVESTIGATION;
                        newEmployeeId = null;
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.APPROVED;
                        break;
                    }
                    case APPROVED: {
                        roleRequired = UserEntity.Role.CHIEF_OPERATIVE;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_OPERATIVE;
                        break;
                    }
                    case ACCEPTED_BY_OPERATIVE: {
                        roleRequired = UserEntity.Role.CHIEF_OPERATIVE;
                        newEmployeeId = null;
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.CAUGHT;
                        break;
                    }
                    case CAUGHT: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    case ACCEPTED_BY_RESEARCHER: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = null;
                        newState = BidEntity.State.DONE;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to change bid state");
                    }
                }
            } else {
                newDescription = oldValue.getDescription();
                type = BidEntity.Type.REPAIR;
                switch (oldValue.getState()) {
                    case PENDING: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    case ACCEPTED_BY_RESEARCHER: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = action == Action.DONE ? null : user.getUserId();
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to change bid state");
                    }
                }
            }
            checkArgument(roleRequired == user.getRole(), new GhostFlowAccessDeniedException());
            TypeStatePair key = new TypeStatePair(type, newState);

            long newLastUpdateTime = Instant.now().toEpochMilli();
            List<UserEntity.Role> roles = WAITING_FOR_TO_ROLES.get(key);

            for (UserEntity.Role role : (roles == null ? Collections.<UserEntity.Role>emptyList() : roles)) {
                RoleLock lock = locks.get(role);
                if (lock != null) {
                    oldSemaphores.add(lock.getSemaphore());
                    lock.setSemaphore(new Semaphore(0));
                    newLastUpdateTime = lock.getNewUpdateTime();
                }
            }

            return new BidEntity<>(
                bidId,
                oldValue.getCustomerId(),
                newEmployeeId,
                newState,
                newLastUpdateTime,
                newDescription,
                null
            );
        });
        oldSemaphores.forEach(s -> s.release(100));
        return bidRepository.extended().findExtended(bidId).get();
    }

    private <T> T updateAndNotify(Function<Long, T> updater, TypeStatePair key) {
        List<UserEntity.Role> roles = WAITING_FOR_TO_ROLES.get(key);
        long newLastUpdateTime = Instant.now().toEpochMilli();
        List<RoleLock> notifiers = new ArrayList<>();

        for (UserEntity.Role role : roles == null ? Collections.<UserEntity.Role>emptyList() : roles) {
            RoleLock lock = locks.get(role);
            notifiers.add(lock);
        }

        if (!notifiers.isEmpty()) {
            newLastUpdateTime = notifiers.get(0).getNewUpdateTime();
        }

        T t = updater.apply(newLastUpdateTime);
        for (RoleLock lock : notifiers) {
            Semaphore oldSemaphore = lock.getSemaphore();
            lock.setSemaphore(new Semaphore(0));
            oldSemaphore.release(100);
        }
        return t;
    }

    @Override
    public void delete(String email, long id) {
        UserEntity user = userService.get(email);
        bidRepository.delete(id, bid -> {
            checkArgument(Objects.equals(bid.getCustomerId(), user.getUserId()), new GhostFlowAccessDeniedException());
            checkArgument(bid.getState() == BidEntity.State.PENDING, "Unable to delete accepted bid");
            return true;
        });
    }

    @Getter
    private static class RoleLock {
        private static final AtomicLong LAST_UPDATE_TIME = new AtomicLong(0);

        @Setter
        private Semaphore semaphore = new Semaphore(0);

        public static long getLastUpdateTime() {
            return LAST_UPDATE_TIME.get();
        }

        private synchronized long getNewUpdateTime() {
            return LAST_UPDATE_TIME.updateAndGet(v -> {
                long updateTime = Instant.now().toEpochMilli();
                if (updateTime == v) {
                    updateTime++;
                }
                return updateTime;
            });
        }
    }
}
