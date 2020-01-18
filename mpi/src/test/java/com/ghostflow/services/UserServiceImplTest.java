package com.ghostflow.services;

import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserService userService;

    @Test
    public void createUser() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "CLIENT");
        UserService.UserInfo info = new UserService.UserInfo(ent);
        Mockito.when(userRepository.find(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.create(anyString(), eq("kek"), anyString(),
            eq(UserEntity.Role.CLIENT))).thenReturn(ent);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        UserService.UserInfo actualInfo = userService.createUser("kek", "kek", "pass");
        assertEquals(info, actualInfo);
    }

    @Test
    public void get() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "ADMIN");
        Mockito.when(userRepository.find("kek@kek")).thenReturn(Optional.of(ent));

        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        UserEntity actualEnt = userService.get("kek@kek");
        assertEquals(ent, actualEnt);
    }
}