package com.ghostflow.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.CompanyReviewRepository;
import com.ghostflow.database.CompanyReviews;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.database.postgres.entities.ExtendedCompanyReviewEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
public class CompanyReviewServiceImplTest {
    @MockBean
    private CompanyReviewRepository companyReviewRepository;

    @MockBean
    private BidRepository bidRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private CompanyReviewRepository.Extended extended;

    @MockBean
    private BidRepository.Extended bidExtended;

    @MockBean
    private ObjectMapper objectMapper;

    public CompanyReviewServiceImplTest() {}

//    @TestConfiguration
//    class EmployeeServiceImplTestContextConfiguration {
//
//        @Bean
//        public CompanyReviewService companyReviewService() {
//            return new CompanyReviewServiceImpl(bidRepository, userService, repository);
//        }
//    }

//    @Autowired
    private CompanyReviewService companyReviewService;

    @Before
    public void setUp() {
    }

    @Test
    public void create() throws IOException {
        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(objectMapper.readValue("{}", new TypeReference<BidEntity.Description>(){}))
                .thenReturn(new BidEntity.CommonDescription("", "", "", "", "", ""));


        ExtendedBidEntity bid = new ExtendedBidEntity(objectMapper, (long)1, (long)1, (long)1, "DONE",
                (long)0, "{}",
                (long)0, "name", "email", "asd",
                (long)1);
        Mockito.when(bidExtended.findExtended((long)1)).thenReturn(Optional.of(bid));

        Mockito.when(bidRepository.extended()).thenReturn(bidExtended);

        Mockito.when(companyReviewRepository.findByBidId(1)).thenReturn(Optional.empty());
        CompanyReviewEntity ent = new CompanyReviewEntity((long)1, 10, "ololo");
        Mockito.when(companyReviewRepository.create(any())).thenReturn(ent);

        companyReviewService = new CompanyReviewServiceImpl(bidRepository, userService, companyReviewRepository);
        CompanyReviewEntity actualEntity = companyReviewService.create("kek@kek", 1, 10, "ololo");
        assertEquals(ent, actualEntity);
    }

    @Test
    public void all() {
        List<ExtendedCompanyReviewEntity> reviewsList = new ArrayList<>();
        reviewsList.add(new ExtendedCompanyReviewEntity((long)1, 1, 100, "lol", Timestamp.from(Instant.EPOCH), "user"));
        CompanyReviews reviews = new CompanyReviews(reviewsList, reviewsList.size());
        Mockito.when(companyReviewRepository.extended())
                .thenReturn(extended);
        Mockito.when(extended.all(0,10)).thenReturn(reviews);
        companyReviewService = new CompanyReviewServiceImpl(bidRepository, userService, companyReviewRepository);

        CompanyReviews actualReviews = companyReviewService.all(0, 10);

        assertEquals(actualReviews, reviews);

    }
}