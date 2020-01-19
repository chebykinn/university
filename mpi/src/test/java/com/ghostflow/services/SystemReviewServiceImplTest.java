package com.ghostflow.services;

import com.ghostflow.database.SystemReviewRepository;
import com.ghostflow.database.postgres.entities.SystemReviewEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;

@RunWith(SpringRunner.class)
public class SystemReviewServiceImplTest {

    @MockBean
    private UserService userService;

    @MockBean
    private SystemReviewRepository systemReviewRepository;

    private SystemReviewService systemReviewService;

    @Test
    public void create() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "CLIENT");
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(userService.get("kek@kek")).thenReturn(ent);
        Mockito.when(systemReviewRepository.findByUserId(1)).thenReturn(Optional.empty());
        Mockito.when(systemReviewRepository.create(new SystemReviewEntity((long)1, 10, "review")))
                .thenReturn(rev);
        systemReviewService = new SystemReviewServiceImpl(userService, systemReviewRepository);
        SystemReviewEntity actualRev = systemReviewService.create("kek@kek", 10, "review");
        assertEquals(rev, actualRev);
    }

    @Test
    public void update() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "CLIENT");
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        SystemReviewEntity rev2 = new SystemReviewEntity(1, 9, "review2");
        Mockito.when(userService.get("kek@kek")).thenReturn(ent);
        Mockito.when(systemReviewRepository.findByUserId(1)).thenReturn(Optional.of(rev));
        Mockito.when(systemReviewRepository.update(1, 9, "review2"))
                .thenReturn(Optional.of(rev2));
        systemReviewService = new SystemReviewServiceImpl(userService, systemReviewRepository);
        SystemReviewEntity actualRev = systemReviewService.update("kek@kek", 9, "review2");
        assertEquals(rev2, actualRev);
    }

    @Test
    public void get() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "CLIENT");
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(userService.get("kek@kek")).thenReturn(ent);
        Mockito.when(systemReviewRepository.findByUserId(1)).thenReturn(Optional.of(rev));
        systemReviewService = new SystemReviewServiceImpl(userService, systemReviewRepository);
        SystemReviewEntity actualRev = systemReviewService.get("kek@kek");
        assertEquals(rev, actualRev);
    }

    @Test
    public void delete() {
        UserEntity ent = new UserEntity((long)1, "kek@kek", "kek", "pass", "CLIENT");
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(userService.get("kek@kek")).thenReturn(ent);
        Mockito.when(systemReviewRepository.findByUserId(1)).thenReturn(Optional.of(rev));
        Mockito.doAnswer((i) -> null).when(systemReviewRepository).delete(1);
        systemReviewService = new SystemReviewServiceImpl(userService, systemReviewRepository);
        systemReviewService.delete("kek@kek");
    }
}