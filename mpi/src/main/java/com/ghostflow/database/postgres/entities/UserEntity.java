package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ghostflow.database.Utils;
import com.ghostflow.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class UserEntity {
    private Long      userId;
    private String    email;
    private String    name;
    @Getter(onMethod = @__({@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)}))
    private String    password;
    private Role      role;
    private boolean   approved;

    public UserEntity(String email, String name, String password, Role role) {
        this(null, email, name, password, role, false);
    }

    public UserEntity(Long userId, String email, String name, String password, String roleStr, boolean approved) {
        this(userId, email, name, password, Role.nullableValueOf(roleStr), approved);
    }

    @JsonIgnore
    public String getRoleStr() {
        return getRole().name();
    }

    @Getter
    public enum Role{
        ADMIN,
        CLIENT,
        INVESTIGATION(new Pair<>(BidEntity.Type.COMMON, BidEntity.State.PENDING)),
        RESEARCH_AND_DEVELOPMENT(new Pair<>(BidEntity.Type.COMMON, BidEntity.State.APPROVED), new Pair<>(BidEntity.Type.REPAIR, BidEntity.State.PENDING)),
        CHIEF_OPERATIVE(new Pair<>(BidEntity.Type.COMMON, BidEntity.State.CAUGHT)),
        OPERATIVE;

        private final List<Pair<BidEntity.Type, BidEntity.State>> waitingFor;
        private final String[] waitingForStr;

        Role(Pair<BidEntity.Type, BidEntity.State> ... pairs) {
            this.waitingFor = Arrays.asList(pairs);
            this.waitingForStr = this.waitingFor.stream().flatMap(p -> Stream.of(p.getFst().toString(), p.getSnd().toString())).toArray(String[]::new);
        }

        public static Role nullableValueOf(String name) {
            return Utils.nullableValueOf(Role.class, name);
        }
    }
}
