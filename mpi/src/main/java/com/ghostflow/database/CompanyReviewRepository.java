package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.database.postgres.entities.ExtendedCompanyReviewEntity;

import java.util.Optional;

public interface CompanyReviewRepository {
    CompanyReviewEntity create(CompanyReviewEntity entity);

    Optional<CompanyReviewEntity> find(long id);

    Optional<CompanyReviewEntity> findByBidId(long bidId);

    Optional<CompanyReviewEntity> update(long companyReviewId, int rating, String review);

    void delete(long id);

    CompanyReviewRepository.Extended extended();

    interface Extended {
        Optional<ExtendedCompanyReviewEntity> findExtended(long id);

        CompanyReviews all(int offset, int limit);
    }
}
