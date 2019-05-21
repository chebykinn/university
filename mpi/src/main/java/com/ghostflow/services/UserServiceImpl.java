package com.ghostflow.services;

import com.ghostflow.GhostFlowException;
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
import static com.google.common.base.Preconditions.checkNotNull;

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
    public void create(String email, String name, String password, UserEntity.Role role) {
        checkArgument(!(email == null || email.trim().isEmpty()), "email is empty");
        checkArgument(!(name == null || name.trim().isEmpty()), "name is empty");
        checkArgument(!(password == null || password.trim().isEmpty()), "password is empty");
        checkNotNull(role, "role is null");
        checkArgument(role != UserEntity.Role.ADMIN, "unable to create admin account");

        checkArgument(!userRepository.find(email).isPresent(), "user already exists");

        userRepository.create(new UserEntity(email, name, bCryptPasswordEncoder.encode(password), role));

        USER_CACHE.invalidate(email);
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
        return USER_CACHE.getUnchecked(email).orElseThrow(() -> new GhostFlowException("unknown user id"));
    }

    @Override
    public Employees getEmployees(String email, long limit, long offset) {
        checkArgument(get(email).getRole() == UserEntity.Role.ADMIN, new GhostFlowAccessDeniedException());
        return userRepository.allEmployees(limit, offset);
    }

    @Override
    public UserEntity update(String email, long userId, boolean approved) {
        checkArgument(get(email).getRole() == UserEntity.Role.ADMIN, new GhostFlowAccessDeniedException());
        UserEntity result = userRepository.update(userId, approved).orElseThrow(() -> new IllegalArgumentException("Unknown user id"));
        USER_CACHE.invalidate(email);
        return result;
    }
}
