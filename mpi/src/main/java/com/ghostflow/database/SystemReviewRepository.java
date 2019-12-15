package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.ExtendedSystemReviewEntity;
import com.ghostflow.database.postgres.entities.SystemReviewEntity;

import java.util.List;
import java.util.Optional;

public interface SystemReviewRepository {
    SystemReviewEntity create(SystemReviewEntity entity);

    Optional<SystemReviewEntity> find(long id);

    Optional<SystemReviewEntity> findByUserId(long userId);

    Optional<SystemReviewEntity> update(long systemReviewId, int rating, String review);

    void delete(long id);

    SystemReviewRepository.Extended extended();

    interface Extended {
        Optional<ExtendedSystemReviewEntity> findExtended(long id);

        List<ExtendedSystemReviewEntity> all(int offset, int limit);
    }
}
