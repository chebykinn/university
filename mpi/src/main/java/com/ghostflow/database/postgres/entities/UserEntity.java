package com.ghostflow.database.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ghostflow.database.Utils;
import com.ghostflow.utils.TypeStatePair;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class UserEntity {
    private Long      userId;
    private String    email;
    private String    name;
    @Getter(AccessLevel.NONE)
    private String    password;
    private Role      role;

    public UserEntity(String email, String name, String password, Role role) {
        this(null, email, name, password, role);
    }

    public UserEntity(Long userId, String email, String name, String password, String roleStr) {
        this(userId, email, name, password, Role.nullableValueOf(roleStr));
    }

    @JsonIgnore
    public String getRoleStr() {
        return getRole().name();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(password, that.password) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, name, password, role);
    }

    @Getter
    public enum Role{
        ADMIN,
        CLIENT,
        INVESTIGATION(new TypeStatePair(BidEntity.Type.COMMON, BidEntity.State.PENDING)),
        RESEARCH_AND_DEVELOPMENT(new TypeStatePair(BidEntity.Type.COMMON, BidEntity.State.CAUGHT), new TypeStatePair(BidEntity.Type.REPAIR, BidEntity.State.PENDING)),
        CHIEF_OPERATIVE(new TypeStatePair(BidEntity.Type.COMMON, BidEntity.State.APPROVED)),
        OPERATIVE(new TypeStatePair(BidEntity.Type.COMMON, BidEntity.State.ACCEPTED_BY_OPERATIVE));

        private final List<TypeStatePair> waitingFor;

        Role(TypeStatePair ... pairs) {
            this.waitingFor = Arrays.asList(pairs);
        }

        public static String[] waitingForToStr(Stream<TypeStatePair> waitingFor) {
            return waitingFor.flatMap(p -> Stream.of(p.getType().toString(), p.getState().toString())).toArray(String[]::new);
        }

        public static Role nullableValueOf(String name) {
            return Utils.nullableValueOf(Role.class, name);
        }
    }
}
