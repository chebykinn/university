package com.ghostflow.services;

import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;

public interface LongPollingBidService {
    Bids getCreatedBids(String email, long limit, long offset);

    Bids getBidsByRole(String email, long limit, long offset);

    Bids waitForNewBidsByRole(String email, long lastUpdateTime);

    ExtendedBidEntity createBid(String email, BidEntity.Description description);

    ExtendedBidEntity updateBid(String email, long bidId, BidEntity.Description description, Action action);

    void delete(String email, long id);
}
