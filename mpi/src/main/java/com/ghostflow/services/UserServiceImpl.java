package com.ghostflow.services;

import com.ghostflow.database.Employees;
import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.http.security.GhostFlowAccessDeniedException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    private final LoadingCache<String, Optional<UserEntity>> USER_CACHE = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<String, Optional<UserEntity>>() {
            @Override
            public Optional<UserEntity> load(String email) throws Exception {
                return userRepository.find(email);
            }
        });

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserInfo createUser(String email, String name, String password) {
        return create(email, name, password, UserEntity.Role.CLIENT);
    }

    @Override
    public UserInfo createEmployee(String email, String name, String password) {
        return create(email, name, password, null);
    }

    private UserInfo create(String email, String name, String password, UserEntity.Role role) {
        checkArgument(!(email == null || email.trim().isEmpty()), "email is empty");
        checkArgument(!(name == null || name.trim().isEmpty()), "name is empty");
        checkArgument(!(password == null || password.trim().isEmpty()), "password is empty");

        checkArgument(!userRepository.find(email).isPresent(), "user already exists");

        USER_CACHE.invalidate(email);
        return new UserInfo(userRepository.create(email, name, bCryptPasswordEncoder.encode(password), role));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.find(username);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(optionalUser.get().getEmail(), optionalUser.get().getPassword(), Collections.emptyList());
    }

    @Override
    public UserEntity get(String email) {
        return USER_CACHE.getUnchecked(email).orElseThrow(() -> new IllegalArgumentException("unknown email"));
    }

    @Override
    public Employees getEmployees(String email, long limit, long offset) {
        checkArgument(get(email).getRole() == UserEntity.Role.ADMIN, new GhostFlowAccessDeniedException());
        return userRepository.allEmployees(limit, offset);
    }

    @Override
    public UserEntity approve(String email, long userId, UserEntity.Role role) {
        checkArgument(get(email).getRole() == UserEntity.Role.ADMIN, new GhostFlowAccessDeniedException());
        UserEntity result = userRepository.approve(userId, role).orElseThrow(() -> new IllegalArgumentException("Unknown user id"));
        USER_CACHE.invalidate(email);
        return result;
    }

    @Override
    public void delete(String email, long id) {
        checkArgument(get(email).getRole() == UserEntity.Role.ADMIN, new GhostFlowAccessDeniedException());
        userRepository.delete(id);
    }
}
