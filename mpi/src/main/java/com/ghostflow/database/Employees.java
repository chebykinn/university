package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class Employees {
    private final List<UserEntity> employees;
    private final long count;
}
