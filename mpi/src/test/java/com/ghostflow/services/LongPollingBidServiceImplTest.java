package com.ghostflow.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

@RunWith(SpringRunner.class)
public class LongPollingBidServiceImplTest {

    @MockBean
    private UserService userService;

    @MockBean
    private BidRepository bidRepository;

    @MockBean
    private BidRepository.Extended extended;

    @MockBean
    private ObjectMapper objectMapper;

    private LongPollingBidService longPollingBidService;

    @Test
    public void getCreatedBids() {
        ExtendedBidEntity<BidEntity.CommonDescription> ent = new ExtendedBidEntity<>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);
        Bids bids = new Bids(Collections.singletonList(ent), 0);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);

        Mockito.when(extended.findByCustomerExtended(1, 10, 0)).thenReturn(bids);

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        Bids actualBids = longPollingBidService.getCreatedBids("kek@kek", 10, 0);
        assertEquals(bids, actualBids);
    }

    @Test
    public void getAcceptedBids() {
        ExtendedBidEntity<BidEntity.CommonDescription> ent = new ExtendedBidEntity<>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);
        Bids bids = new Bids(Collections.singletonList(ent), 0);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);

        Mockito.when(extended.findByEmployeeExtended(1, 10, 0)).thenReturn(bids);

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        Bids actualBids = longPollingBidService.getAcceptedBids("kek@kek", 10, 0);
        assertEquals(bids, actualBids);
    }

    @Test
    public void getBid() {
        ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.CommonDescription>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "CLIENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);
        Mockito.when(extended.findExtended(1)).thenReturn(java.util.Optional.of(ent));

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        BidEntity<?> actualBid = longPollingBidService.getBid("kek@kek", 1);
        assertEquals(ent, actualBid);
    }

    @Test
    public void getBidsByRole() {
        ExtendedBidEntity<BidEntity.CommonDescription> ent = new ExtendedBidEntity<>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);
        Bids bids = new Bids(Collections.singletonList(ent), 0);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "RESEARCH_AND_DEVELOPMENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);

        Mockito.when(extended.findExtended(anyLong(), anyLong(), anyLong(), anyVararg())).thenReturn(bids);

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        Bids actualBids = longPollingBidService.getBidsByRole("kek@kek", BidEntity.Type.REPAIR, 10, 0);
        assertEquals(bids, actualBids);
    }

    @Test
    public void waitForNewBidsByRole() {
        BidEntity.Description desc = new BidEntity.RepairDescription("", "", "", "", "", "");
        ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.RepairDescription>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);
        Bids bids = new Bids(Collections.singletonList(ent), 0);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "RESEARCH_AND_DEVELOPMENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);
        Mockito.when(bidRepository.create(any())).thenReturn((long) 1);
        Mockito.when(extended.findGtThanUpdateTimeExtended(anyLong(), anyVararg())).thenReturn(bids);

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        Bids actualBids = longPollingBidService.waitForNewBidsByRole("kek@kek", (long) -1);
        assertEquals(bids, actualBids);
    }

    @Test
    public void createBid() {
        BidEntity.Description desc = new BidEntity.RepairDescription("", "", "", "", "", "");
        ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.RepairDescription>(objectMapper, (long) 1,
                (long) 1, (long) 1, "DONE", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "RESEARCH_AND_DEVELOPMENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);
        Mockito.when(bidRepository.create(any())).thenReturn((long) 1);
        Mockito.when(extended.findExtended(1)).thenReturn(java.util.Optional.of(ent));

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        BidEntity<?> actualBid = longPollingBidService.createBid("kek@kek", desc);
        assertEquals(ent, actualBid);
    }

    @Test
    public void updateBid() {
        BidEntity.Description desc = new BidEntity.RepairDescription("", "", "", "", "", "");
        ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.RepairDescription>(objectMapper, (long) 1,
                (long) 1, (long) 1, "PENDING", (long) 1, "desc",
                (long) 1, "customer", "cu@cu", "employee", (long) 1);

        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "RESEARCH_AND_DEVELOPMENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

        Mockito.when(bidRepository.extended()).thenReturn(extended);
        Mockito.when(bidRepository.updateSafely(eq(1), any())).thenReturn(java.util.Optional.of(ent));
        Mockito.when(extended.findExtended(1)).thenReturn(java.util.Optional.of(ent));

        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        BidEntity<?> actualBid = longPollingBidService.updateBid("kek@kek", 1, desc, Action.DONE);
        assertEquals(ent, actualBid);
        BidEntity<?> actualBid2 = longPollingBidService.updateBid("kek@kek", 1, desc, Action.UPDATE);
        assertEquals(ent, actualBid);
    }

    @Test
    public void updateBidCommon() {
        List<String> states = new ArrayList<>();
        states.add("PENDING");
        states.add("ACCEPTED");
        states.add("APPROVED");
        states.add("ACCEPTED_BY_OPERATIVE");
        states.add("CAUGHT");
        states.add("ACCEPTED_BY_RESEARCHER");
        for(String state : states) {
            BidEntity.Description desc = new BidEntity.CommonDescription("", "", "", "", "", "");
            ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.CommonDescription>(objectMapper, (long) 1,
                    (long) 1, (long) 1, state, (long) 1, "desc",
                    (long) 1, "customer", "cu@cu", "employee", (long) 1);

            UserEntity userEntity = new UserEntity((long) 1, "kek@kek", "usr", "pass", "CHIEF_OPERATIVE");
            Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);

            Mockito.when(bidRepository.extended()).thenReturn(extended);
            Mockito.when(bidRepository.updateSafely(eq(1), any())).thenReturn(java.util.Optional.of(ent));
            Mockito.when(extended.findExtended(1)).thenReturn(java.util.Optional.of(ent));

            longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
            BidEntity<?> actualBid = longPollingBidService.updateBid("kek@kek", 1, desc, Action.DONE);
            assertEquals(ent, actualBid);
            BidEntity<?> actualBid2 = longPollingBidService.updateBid("kek@kek", 1, desc, Action.UPDATE);
            assertEquals(ent, actualBid);
        }
    }

    @Test
    public void delete() {
        UserEntity userEntity = new UserEntity((long)1, "kek@kek", "usr", "pass", "RESEARCH_AND_DEVELOPMENT");
        Mockito.when(userService.get("kek@kek")).thenReturn(userEntity);
        Mockito.when(bidRepository.delete(anyLong(), any())).thenReturn(true);
        longPollingBidService = new LongPollingBidServiceImpl(userService, bidRepository);
        longPollingBidService.delete("kek@kek", 1);
    }
}