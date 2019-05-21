package com.ghostflow.services;

import com.ghostflow.database.Employees;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void create(String email, String name, String password, UserEntity.Role role);

    UserEntity get(String email);

    Employees getEmployees(String email, long limit, long offset);

    UserEntity update(String email, long id, boolean approved);
}
