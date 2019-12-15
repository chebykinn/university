package com.ghostflow.services;

import com.ghostflow.database.CompanyReviews;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;

public interface CompanyReviewService {
    CompanyReviewEntity create(String email, long bidId, int rating, String review);

    CompanyReviewEntity update(String email, long bidId, int rating, String review);

    CompanyReviewEntity get(String email, long bidId);

    CompanyReviewEntity get(long companyReviewId);

    CompanyReviews all(int offset, int limit);

    void delete(String email, long bidId);
}
