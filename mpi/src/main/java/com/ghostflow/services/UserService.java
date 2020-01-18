package com.ghostflow.services;

import com.ghostflow.database.Employees;
import com.ghostflow.database.postgres.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Objects;

public interface UserService extends UserDetailsService {
    UserInfo createUser(String email, String name, String password);

    UserInfo createEmployee(String email, String name, String password);

    UserEntity get(String email);

    default UserInfo getUserInfo(String email) {
        return new UserInfo(get(email));
    }

    Employees getEmployees(String email, long limit, long offset);

    UserEntity approve(String email, long id, UserEntity.Role role);

    void delete(String email, long id);

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    class UserInfo {
        private Long      userId;
        private String    email;
        private String    name;

        public UserInfo(UserEntity entity) {
            this(entity.getUserId(), entity.getEmail(), entity.getName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserInfo userInfo = (UserInfo) o;
            return Objects.equals(userId, userInfo.userId) &&
                    Objects.equals(email, userInfo.email) &&
                    Objects.equals(name, userInfo.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, email, name);
        }
    }
}
