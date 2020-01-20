package com.ghostflow.integrational.http.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.CompanyReviewRepository;
import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.http.beans.Review;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.jws.soap.SOAPBinding;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:ghostflow-integrationtest.properties"
)
@AutoConfigureEmbeddedDatabase
public class CompanyReviewControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyReviewRepository companyReviewRepository;

    @Autowired
    private BidRepository bidRepository;

    @Before
    public void setUp() throws JsonProcessingException {
        Optional<UserEntity> lol = userRepository.find("lol@lol");
        Optional<UserEntity> op = userRepository.find("kek@kek");
        if (!op.isPresent()) {
            op = Optional.ofNullable(userRepository.create("kek@kek", "admin", "pass", UserEntity.Role.CHIEF_OPERATIVE));
        }
        if (!lol.isPresent()) {
            lol = Optional.ofNullable(userRepository.create("lol@lol", "client", "pass", UserEntity.Role.CLIENT));
        }
        if (!bidRepository.extended().findExtended(1).isPresent()) {
            BidEntity.CommonDescription desc = new BidEntity.CommonDescription("", "", "", "", "", "");
            ObjectMapper objectMapper = new ObjectMapper();
            long bidEntity = bidRepository.create(new BidEntity<BidEntity.CommonDescription>(objectMapper,
                    (long)1, lol.get().getUserId(), op.get().getUserId(), "PENDING", 0,
                    objectMapper.writeValueAsString(desc), (long)0));

        }
    }

    @Test
    @WithMockUser(username = "lol@lol")
    public void testGet() throws Exception {
        if (!companyReviewRepository.find(1).isPresent()) {
            CompanyReviewEntity ent = companyReviewRepository.create(new CompanyReviewEntity(1, 10, "review"));
        }
        mvc.perform(get("/api/bids/1/review")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review", is("review")));
    }

    @Test
    @WithMockUser(username = "lol@lol")
    public void create() throws Exception {
        mvc.perform(post("/api/bids/1/review")
                .contentType(MediaType.APPLICATION_JSON).content(
                        "{\"review\":\"review text\",\"rating\":10}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lol@lol")
    public void update() throws Exception {
        if (!companyReviewRepository.find(1).isPresent()) {
            CompanyReviewEntity ent = companyReviewRepository.create(new CompanyReviewEntity(1, 10, "review"));
        }
        mvc.perform(put("/api/bids/1/review")
                .contentType(MediaType.APPLICATION_JSON).content(
                        "{\"review\":\"review text\",\"rating\":10}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lol@lol")
    public void testDelete() throws Exception {
        if (!companyReviewRepository.find(1).isPresent()) {
            CompanyReviewEntity ent = companyReviewRepository.create(new CompanyReviewEntity(1, 10, "review"));
        }
        mvc.perform(delete("/api/bids/1/review"))
                .andExpect(status().isOk());
    }
}