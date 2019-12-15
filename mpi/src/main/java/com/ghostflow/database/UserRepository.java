package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> find(long id);

    Optional<UserEntity> find(String email);

    Employees allEmployees(long limit, long offset);

    boolean authorize(String email, String password);

    UserEntity create(String email, String name, String password, UserEntity.Role role);

    Optional<UserEntity> approve(long id, UserEntity.Role role);

    void delete(long id);
}
