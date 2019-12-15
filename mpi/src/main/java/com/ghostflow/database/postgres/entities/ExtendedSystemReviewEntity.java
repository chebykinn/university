package com.ghostflow.database.postgres.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(force = true)
public class ExtendedSystemReviewEntity extends SystemReviewEntity {
    private final UserEntity.Role userRole;
    private final String userName;

    public ExtendedSystemReviewEntity(Long systemReviewId, long userId, int rating, String review, Timestamp updateTime, String userRoleStr, String userName) {
        super(systemReviewId, userId, rating, review, updateTime);
        this.userRole = UserEntity.Role.nullableValueOf(userRoleStr);
        this.userName = userName;
    }
}
