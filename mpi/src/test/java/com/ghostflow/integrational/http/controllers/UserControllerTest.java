package com.ghostflow.integrational.http.controllers;

import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.entities.UserEntity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:ghostflow-integrationtest.properties"
)
@AutoConfigureEmbeddedDatabase
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        if (!userRepository.find("kek@kek").isPresent()) {
            UserEntity ent = userRepository.create("kek@kek", "admin", "pass", UserEntity.Role.ADMIN);
        }
    }

    @Test
    @WithMockUser(username = "kek@kek")
    public void testGet() throws Exception {
        mvc.perform(get("/api/users/?email=kek@kek")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("admin")));
    }

    @Test
    public void createUser() {
    }

    @Test
    public void createEmployee() {
    }

    @Test
    public void getEmployees() throws Exception {
    }

    @Test
    public void approve() {
    }

    @Test
    public void delete() {
    }
}