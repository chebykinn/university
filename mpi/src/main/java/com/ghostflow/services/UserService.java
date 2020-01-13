package com.ghostflow.services;

import com.ghostflow.database.Employees;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserEntity createUser(String email, String name, String password);

    UserEntity createEmployee(String email, String name, String password);

    UserEntity get(String email);

    Employees getEmployees(String email, long limit, long offset);

    UserEntity approve(String email, long id, UserEntity.Role role);

    void delete(String email, long id);
}
