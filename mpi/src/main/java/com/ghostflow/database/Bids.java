package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(force = true)
@Getter
public class Bids {
    private final List<ExtendedBidEntity> bids;
    private final Long count;
    private final Long lastUpdateTime;

    public Bids(List<ExtendedBidEntity> bids, Long count, Long lastUpdateTime) {
        this.bids = bids;
        this.count = count;
        this.lastUpdateTime = lastUpdateTime == null ? null : Long.max(bids.stream().mapToLong(BidEntity::getUpdateTime).max().orElse(lastUpdateTime), lastUpdateTime);
    }

    public Bids(List<ExtendedBidEntity> bids, Long count, Timestamp lastUpdateTime) {
        this(bids, count, lastUpdateTime == null ? null : lastUpdateTime.getTime());
    }


    public Bids(Long count, long lastUpdateTime) {
        this(Collections.emptyList(), count, lastUpdateTime);
    }

    public Bids(List<ExtendedBidEntity> bids, long lastUpdateTime) {
        this(bids, null, lastUpdateTime);
    }
}
