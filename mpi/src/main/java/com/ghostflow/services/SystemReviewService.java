package com.ghostflow.services;

import com.ghostflow.database.postgres.entities.SystemReviewEntity;

public interface SystemReviewService {
    SystemReviewEntity create(String email, int rating, String review);

    SystemReviewEntity get(String email);

    SystemReviewEntity update(String email, int rating, String review);

    void delete(String email);
}
