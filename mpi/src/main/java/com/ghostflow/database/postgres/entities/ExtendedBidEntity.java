package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ExtendedBidEntity<T extends BidEntity.Description> extends BidEntity<T> {
    private final String customerName;
    private final String employeeName;

    public ExtendedBidEntity(ObjectMapper objectMapper, Long bidId, Long customerId, Long employeeId, String stateStr, long updateTime, String descriptionStr, String customerName, String employeeName) {
        super(objectMapper, bidId, customerId, employeeId, stateStr, updateTime, descriptionStr);
        this.customerName = customerName;
        this.employeeName = employeeName;
    }
}
