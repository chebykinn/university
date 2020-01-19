package com.ghostflow.database.postgres.repositories;

import com.ghostflow.database.SystemReviewRepository;
import com.ghostflow.database.postgres.entities.ExtendedSystemReviewEntity;
import com.ghostflow.database.postgres.entities.SystemReviewEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

@RunWith(SpringRunner.class)
public class SystemReviewRepositoryImplTest {
    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SystemReviewRepository systemReviewRepository;

    @Test
    public void create() {
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(jdbcTemplate.queryForObject(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(rev);

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        SystemReviewEntity actualRev = systemReviewRepository.create(rev);
        assertEquals(rev, actualRev);
    }

    @Test
    public void find() {
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(rev));

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        Optional<SystemReviewEntity> actualRev = systemReviewRepository.find(1);
        assertEquals(rev, actualRev.get());
    }

    @Test
    public void findExtended() {
        ExtendedSystemReviewEntity rev = new ExtendedSystemReviewEntity((long) 1, 1, 10, "review", null, "CLIENT", "kek");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(rev));

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        Optional<ExtendedSystemReviewEntity> actualRev = systemReviewRepository.extended().findExtended(1);
        assertEquals(rev, actualRev.get());
    }

    @Test
    public void all() {
        ExtendedSystemReviewEntity rev = new ExtendedSystemReviewEntity((long) 1, 1, 10, "review", null, "CLIENT", "kek");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(rev));

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        List<ExtendedSystemReviewEntity> actualRev = systemReviewRepository.extended().all(0, 10);
        assertEquals(rev, actualRev.get(0));

    }

    @Test
    public void findByUserId() {
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(rev));

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        Optional<SystemReviewEntity> actualRev = systemReviewRepository.findByUserId(1);
        assertEquals(rev, actualRev.get());
    }

    @Test
    public void update() {
        SystemReviewEntity rev = new SystemReviewEntity(1, 10, "review");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(rev));

        systemReviewRepository = new SystemReviewRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        Optional<SystemReviewEntity> actualRev = systemReviewRepository.update(1, 10, "review");
        assertEquals(rev, actualRev.get());
    }
}