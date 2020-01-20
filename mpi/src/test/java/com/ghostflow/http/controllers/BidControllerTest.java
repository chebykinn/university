package com.ghostflow.http.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.services.LongPollingBidService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BidController.class)
public class BidControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private LongPollingBidService bidService;

    @MockBean
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
    }

    private void getImpl(String path) throws Exception {
    }

    @Test
    @WithMockUser()
    public void getCommon() throws Exception {
        Mockito.when(objectMapper.readValue("{}", new TypeReference<BidEntity.Description>(){}))
                .thenReturn(new BidEntity.CommonDescription("", "", "", "", "", ""));


        ExtendedBidEntity bid = new ExtendedBidEntity(objectMapper, (long)1, (long)1, (long)1, "DONE",
                (long)0, "{}",
                (long)0, "name", "email", "asd",
                (long)1);
        List<ExtendedBidEntity> bidsList = new ArrayList<>();
        bidsList.add(bid);
        Bids bids = new Bids(bidsList, (long)bidsList.size(), (long)0);

        Mockito.when(bidService.getBidsByRole(anyString(), any(), anyLong(), anyLong())).thenReturn(bids);

        mvc.perform(get("/api/bids/common").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bids[0].bidId", is(1)))
                .andExpect(jsonPath("$.bids[0].state", is("DONE")));
    }

    @Test
    @WithMockUser()
    public void getCreated() throws Exception {
        Mockito.when(objectMapper.readValue("{}", new TypeReference<BidEntity.Description>(){}))
                .thenReturn(new BidEntity.CommonDescription("", "", "", "", "", ""));


        ExtendedBidEntity bid = new ExtendedBidEntity(objectMapper, (long)1, (long)1, (long)1, "DONE",
                (long)0, "{}",
                (long)0, "name", "asd@as", "emp", (long) 1);
        List<ExtendedBidEntity> bidsList = new ArrayList<>();
        bidsList.add(bid);
        Bids bids = new Bids(bidsList, (long)bidsList.size(), (long)0);

        Mockito.when(bidService.getCreatedBids(anyString(), anyLong(), anyLong())).thenReturn(bids);

        mvc.perform(get("/api/bids/created").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bids[0].bidId", is(1)))
                .andExpect(jsonPath("$.bids[0].state", is("DONE")));
    }

    @Test
    @WithMockUser()
    public void getAccepted() throws Exception {
        Mockito.when(objectMapper.readValue("{}", new TypeReference<BidEntity.Description>(){}))
                .thenReturn(new BidEntity.CommonDescription("", "", "", "", "", ""));


        ExtendedBidEntity bid = new ExtendedBidEntity(objectMapper, (long)1, (long)1, (long)1, "DONE",
                (long)0, "{}",
                (long)0, "name", "asd@as", "emp", (long) 1);
        List<ExtendedBidEntity> bidsList = new ArrayList<>();
        bidsList.add(bid);
        Bids bids = new Bids(bidsList, (long)bidsList.size(), (long)0);

        Mockito.when(bidService.getAcceptedBids(anyString(), anyLong(), anyLong())).thenReturn(bids);

        mvc.perform(get("/api/bids/accepted").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bids[0].bidId", is(1)))
                .andExpect(jsonPath("$.bids[0].state", is("DONE")));
    }


    @Test
    @WithMockUser()
    public void getRepair() throws Exception {
        Mockito.when(objectMapper.readValue("{}", new TypeReference<BidEntity.Description>(){}))
                .thenReturn(new BidEntity.CommonDescription("", "", "", "", "" ,""));


        ExtendedBidEntity bid = new ExtendedBidEntity(objectMapper, (long)1, (long)1, (long)1, "DONE",
                (long)0, "{}",
                (long)0, "name", "asd@as", "emp", (long) 1);
        List<ExtendedBidEntity> bidsList = new ArrayList<>();
        bidsList.add(bid);
        Bids bids = new Bids(bidsList, (long)bidsList.size(), (long)0);

        Mockito.when(bidService.getBidsByRole(anyString(), any(), anyLong(), anyLong())).thenReturn(bids);

        mvc.perform(get("/api/bids/repair").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bids[0].bidId", is(1)))
                .andExpect(jsonPath("$.bids[0].state", is("DONE")));
    }

    @Test
    @WithMockUser()
    public void create() throws Exception {
//        BidEntity.Description desc = new BidEntity.CommonDescription("", "", "", "", "", "");
//        ExtendedBidEntity ent = new ExtendedBidEntity<BidEntity.CommonDescription>(objectMapper, (long)1,
//                (long)1, (long)1,"DONE", (long)1, "desc",
//                (long)1, "customer", "cu@cu", "em", (long) 1);
//        Mockito.when(bidService.createBid("kek@kek", desc)).thenReturn(ent);
//
//        String json = "{\"title\": \"\",\"phoneNumber\": \"address\",\"\": \"body\",\"\": \"ghostDescription\"}";
//
//        mvc.perform(post("/api/bids/").contentType(MediaType.APPLICATION_JSON).content(json))
//                .andExpect(status().isOk());
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }
}
