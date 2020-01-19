package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface BidRepository {
    long create(BidEntity<?> entity);

    @Transactional
    Optional<BidEntity<?>> updateSafely(long id, Function<BidEntity<?>, BidEntity<?>> updater) ;

    @Transactional
    boolean delete(long id, Predicate<BidEntity<?>> checker);

    Extended extended();

    interface Extended {
        <T extends BidEntity.Description> Optional<ExtendedBidEntity<T>> findExtended(long id);

        Bids findGtThanUpdateTimeExtended(long updateTime, String... typesStates);

        Bids findByCustomerExtended(long ownerId, long limit, long offset);

        Bids findByEmployeeExtended(long employeeId, long limit, long offset);

        Bids findExtended(long limit, long offset, long userId, String... typesStates);
    }
}
