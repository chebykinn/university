package com.ghostflow.database.postgres.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class CompanyReviewEntity {
    private final Long      companyReviewId;
    private final long      bidId;
    private final int       rating;
    private final String    review;
    private final Timestamp updateTime;

    public CompanyReviewEntity(Long userId, int rating, String review) {
        this(null, userId, rating, review, null);
    }
}
