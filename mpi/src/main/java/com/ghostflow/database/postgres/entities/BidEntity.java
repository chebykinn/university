package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.GhostFlowException;
import com.ghostflow.database.Utils;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class BidEntity<T extends BidEntity.Description> {
    private final Long  bidId;
    @JsonIgnore
    private final Long  customerId;
    private final Long  employeeId;
    private final State state;
    private final long  updateTime;
    private final T     description;

    public BidEntity(ObjectMapper objectMapper, Long bidId, Long customerId, Long employeeId, String stateStr, long updateTime, String descriptionStr) {
        this.bidId = bidId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.state = State.valueOf(stateStr);
        this.updateTime = updateTime;
        try {
            this.description = objectMapper.readValue(descriptionStr, new TypeReference<T>() {});
        } catch (Exception e) {
            throw new GhostFlowException("Unable to parse description", e);
        }
    }

    @AllArgsConstructor
    public enum Type {
        COMMON,
        REPAIR;

        public static final Map<Class, Type> fromClass = ImmutableMap.of(
            CommonDescription.class, COMMON,
            RepairDescription.class, REPAIR
        );

        public static Type nullableValueOf(String name) {
            return Utils.nullableValueOf(Type.class, name);
        }
    }

    public Type getType() {
        return description == null ? null : Type.fromClass.get(description.getClass());
    }

    @JsonIgnore
    public String getTypeStr() {
        return getType().name();
    }


    public enum State {
        PENDING, // bid created
        ACCEPTED, // bid accepted by employee
        APPROVED, // client bid approved by employee
        ACCEPTED_BY_OPERATIVE, // client bid accepted by operative
        CAUGHT, // ghost caught
        ACCEPTED_BY_RESEARCHER, // employee repair bid accepted by researcher
        DONE;
    }

    @JsonIgnore
    public String getStateStr() {
        return state == null ? null : state.name();
    }

    @JsonIgnore
    public Timestamp getUpdateTimestamp() {
        return Timestamp.from(Instant.ofEpochMilli(updateTime));
    }


    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = CommonDescription.class, name = "COMMON"),
        @JsonSubTypes.Type(value = RepairDescription.class, name = "REPAIR")
    })
    public static abstract class Description {}

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    public static class CommonDescription extends Description {
        private final String ghostDescription;
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    public static class RepairDescription extends Description {
        private final String title;
        private final String body;
        private final String status;
    }

    @JsonIgnore
    public String getDescriptionStr(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(description);
        } catch (Exception e) {
            throw new GhostFlowException("Unable to write as string description", e);
        }
    }
}
