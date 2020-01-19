package com.ghostflow.database.postgres.repositories;

import com.ghostflow.database.SimpleGhostFlowJdbcCrud;
import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

@RunWith(SpringRunner.class)
public class UserRepositoryImplTest {

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private UserRepository userRepository;

    @Test
    public void create() {
        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Map<String, Object> elem = new HashMap<>();
        List<Map<String, Object>> out = new ArrayList<>();
        elem.put("user_id", (long)1);
        out.add(elem);
        Mockito.when(jdbcTemplate.queryForList(anyString(), (Object) anyVararg())).thenReturn(out);
        userRepository = new UserRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        UserEntity actualEnt = userRepository.create("kek@kek", "usr", "pass", UserEntity.Role.CLIENT);
        assertEquals(userEntity, actualEnt);
    }

    @Test
    public void find() {
        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), any())).thenReturn(Collections.singletonList(userEntity));
        userRepository = new UserRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate);
        Optional<UserEntity> actualEnt = userRepository.find("kek@kek");
        assertTrue(actualEnt.isPresent());
        assertEquals(userEntity, actualEnt.get());
    }
}