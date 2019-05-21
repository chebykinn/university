package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> find(long id);

    Optional<UserEntity> find(String email);

    Employees allEmployees(long limit, long offset);

    boolean authorize(String email, String password);

    void create(UserEntity entity);

    Optional<UserEntity> update(long id, boolean approved);

    void delete(long id);
}
