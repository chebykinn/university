package com.ghostflow.services;

import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;

public interface LongPollingBidService {
    Bids getCreatedBids(String email, long limit, long offset);

    Bids getAcceptedBids(String email, long limit, long offset);

    BidEntity getBid(String email, long id);

    Bids getBidsByRole(String email, BidEntity.Type typeFilter, long limit, long offset);

    Bids waitForNewBidsByRole(String email, Long lastUpdateTime);

    ExtendedBidEntity createBid(String email, BidEntity.Description description);

    ExtendedBidEntity updateBid(String email, long bidId, BidEntity.Description description, Action action);

    void delete(String email, long id);
}
