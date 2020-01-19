package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;


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
        this((long)1, userId, rating, review, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemReviewEntity that = (SystemReviewEntity) o;
        return userId == that.userId &&
                rating == that.rating &&
                Objects.equals(review, that.review);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemReviewId, userId, rating, review, updateTime);
    }
}
