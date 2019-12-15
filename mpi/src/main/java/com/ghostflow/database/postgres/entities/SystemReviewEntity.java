package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class SystemReviewEntity {
    private final Long      systemReviewId;
    @JsonIgnore
    private final long      userId;
    private final int       rating;
    private final String    review;
    private final Timestamp updateTime;

    public SystemReviewEntity(long userId, int rating, String review) {
        this(null, userId, rating, review, null);
    }
}
