package com.ghostflow.database.postgres.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(force = true)
public class ExtendedCompanyReviewEntity extends CompanyReviewEntity {
    private final String userName;

    public ExtendedCompanyReviewEntity(Long systemReviewId, long userId, int rating, String review, Timestamp updateTime, String userName) {
        super(systemReviewId, userId, rating, review, updateTime);
        this.userName = userName;
    }
}
