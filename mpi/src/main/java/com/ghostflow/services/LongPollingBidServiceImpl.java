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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service
public class LongPollingBidServiceImpl implements LongPollingBidService {

    private static final int TIMEOUT_SECONDS = 100;

    private final UserService userService;
    private final BidRepository bidRepository;

    private final Map<TypeStatePair, RoleLock> locks = new HashMap<>();


    @Autowired
    public LongPollingBidServiceImpl(UserService userService, BidRepository bidRepository) {
        this.userService = userService;
        this.bidRepository = bidRepository;
        for (UserEntity.Role role : UserEntity.Role.values()) {
            if (!role.getWaitingFor().isEmpty()) {
                RoleLock lock = new RoleLock();
                role.getWaitingFor().forEach(p -> locks.put(p, lock));
            }
        }
    }

    @Override
    public Bids getCreatedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByCustomerExtended(user.getUserId(), limit, offset);
    }


    @Override
    public Bids getAcceptedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByEmployeeExtended(user.getUserId(), limit, offset);
    }

    @Override
    public BidEntity getBid(String email, long id) {
        UserEntity user = userService.get(email);
        ExtendedBidEntity bid = bidRepository.extended().findExtended(id).orElseThrow(() -> new IllegalArgumentException("unknown bid id"));

        if(Objects.equals(user.getUserId(), bid.getCustomerId())
            || Objects.equals(user.getUserId(), bid.getEmployeeId())
            || user.getRole().getWaitingFor().stream().anyMatch(r -> bid.getType() == r.getType() || bid.getState() == r.getState())) {
            return bid;
        } else {
            throw new GhostFlowAccessDeniedException();
        }
    }

    @Override
    public Bids getBidsByRole(String email, BidEntity.Type typeFilter, long limit, long offset) {
        UserEntity user = userService.get(email);
        checkArgument(user.isApproved(), new GhostFlowAccessDeniedException());
        return bidRepository.extended().findExtended(limit, offset, getTypesStatesByRole(user.getRole(), typeFilter));
    }

    @Override
    public Bids waitForNewBidsByRole(String email, Long lastUpdateTime) {
        if (lastUpdateTime == null) {
            return new Bids((Long) null, System.currentTimeMillis());
        }
        UserEntity user = userService.get(email);
        checkArgument(user.isApproved(), new GhostFlowAccessDeniedException());
        String[] typesStates = getTypesStatesByRole(user.getRole(), null);
        RoleLock lock = locks.get(user.getRole().getWaitingFor().get(0));
        Semaphore semaphore = lock.getSemaphore();
        boolean acquired = false;
        try {
            if (lastUpdateTime > lock.getLastUpdateTime().get()) {
                acquired = semaphore.tryAcquire(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    return new Bids((Long) null, lastUpdateTime);
                }
            }
        } catch (InterruptedException e) {
            log.error("TryLock interrupted", e);
            return new Bids((Long) null, lastUpdateTime);
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
    public ExtendedBidEntity createBid(String email, BidEntity.Description description) {
        UserEntity user = userService.get(email);
        BidEntity.Type type = BidEntity.Type.fromClass.get(description.getClass());
        switch (type) {
            case REPAIR: {
                checkArgument(user.isApproved(), new GhostFlowAccessDeniedException());
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
            BidEntity bid = new BidEntity<>(null, user.getUserId(), null, BidEntity.State.PENDING, updateTime, description, null);
            return bidRepository.create(bid);
        }, key);
        return bidRepository.extended().findExtended(id).get();
    }

    @Override
    public ExtendedBidEntity updateBid(String email, long bidId, BidEntity.Description description, Action action) {
        UserEntity user = userService.get(email);
        checkArgument(user.isApproved(), new GhostFlowAccessDeniedException());

        Semaphore[] oldSemaphore = new Semaphore[] { null };
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

            if (newDescription instanceof BidEntity.CommonDescription) {
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
                        roleRequired = UserEntity.Role.CHIEF_OPERATIVE;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    case ACCEPTED_BY_RESEARCHER: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = action == Action.DONE ? null : user.getUserId();
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.CAUGHT;
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

            RoleLock lock = locks.get(key);
            long newLastUpdateTime;
            if (lock != null) {
                oldSemaphore[0] = lock.getSemaphore();
                lock.setSemaphore(new Semaphore(0));
                newLastUpdateTime = lock.getNewUpdateTime();
            } else {
                newLastUpdateTime = Instant.now().toEpochMilli();
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
        if (oldSemaphore[0] != null) {
            oldSemaphore[0].release(100);
        }
        return bidRepository.extended().findExtended(bidId).get();
    }

    private <T> T updateAndNotify(Function<Long, T> updater, TypeStatePair key) {
        RoleLock lock = locks.get(key);

        T t = updater.apply(lock.getNewUpdateTime());
        Semaphore oldSemaphore = lock.getSemaphore();
        lock.setSemaphore(new Semaphore(0));
        oldSemaphore.release(100);
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
    private class RoleLock {
        @Setter
        private Semaphore semaphore = new Semaphore(0);
        private final AtomicLong lastUpdateTime = new AtomicLong(0);

        private synchronized long getNewUpdateTime() {
            return lastUpdateTime.updateAndGet(v -> {
                long updateTime = Instant.now().toEpochMilli();
                if (updateTime == v) {
                    updateTime++;
                }
                return updateTime;
            });
        }
    }
}
