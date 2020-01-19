package com.ghostflow.database.postgres.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.postgres.entities.BidEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;

@RunWith(SpringRunner.class)
public class BidRepositoryImplTest {

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private ObjectMapper objectMapper;

    private BidRepository bidRepository;

    @Test
    public void create() {
        bidRepository = new BidRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate, objectMapper);
        Map<String, Object> elem = new HashMap<>();
        List<Map<String, Object>> out = new ArrayList<>();
        elem.put("user_id", (long)1);
        out.add(elem);
        Mockito.when(jdbcTemplate.queryForList(anyString(), (Object) anyVararg())).thenReturn(out);

        long id = bidRepository.create(new BidEntity<BidEntity.CommonDescription>(objectMapper, (long)1, (long)1, (long)1,
                "DONE", 0, "", (long)0));
        assertEquals(1, id);
    }

    @Test
    public void delete() {
        BidEntity ent = new BidEntity<BidEntity.CommonDescription>(objectMapper, (long)1, (long)1, (long)1, "DONE", 0, "", (long)0);
        Mockito.when(jdbcTemplate.query(anyString(), (RowMapper<Object>) any(), anyVararg())).thenReturn(Collections.singletonList(ent));
        bidRepository = new BidRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate, objectMapper);
        boolean del = bidRepository.delete(1, bid -> true);
        assertTrue(del);
    }
}