package com.ghostflow.utils;

import com.ghostflow.database.postgres.entities.BidEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TypeStatePair {
    private final BidEntity.Type type;
    private final BidEntity.State state;
}
