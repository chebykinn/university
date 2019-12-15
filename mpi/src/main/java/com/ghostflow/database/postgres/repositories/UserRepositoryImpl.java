package com.ghostflow.database.postgres.repositories;

import com.ghostflow.database.Employees;
import com.ghostflow.database.SimpleGhostFlowJdbcCrud;
import com.ghostflow.database.UserRepository;
import com.ghostflow.database.postgres.ColumnsBuilder;
import com.ghostflow.database.postgres.ColumnsBuilder.Column;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {

    protected static final ColumnsBuilder columnsBuilder = new ColumnsBuilder();

    protected static final String TABLE_NAME = "users";

    protected static final Column COLUMN_USER_ID    = columnsBuilder.newColumn("user_id");
    protected static final Column COLUMN_EMAIL      = columnsBuilder.newColumn("email");
    protected static final Column COLUMN_PASSWORD   = columnsBuilder.newColumn("password");
    protected static final Column COLUMN_NAME       = columnsBuilder.newColumn("name");
    protected static final Column COLUMN_ROLE       = columnsBuilder.newColumn("role");

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleGhostFlowJdbcCrud<UserEntity> ghostFlowJdbcCrud;
    private final RowMapper<UserEntity> rowMapper;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.ghostFlowJdbcCrud = SimpleGhostFlowJdbcCrud.<UserEntity>builder()
            .withTableName(TABLE_NAME)
            .withIdColumn(COLUMN_USER_ID)
            .withColumns(COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_NAME, COLUMN_ROLE)
            .build(jdbcTemplate);
        this.rowMapper = (rs, rowNum) -> new UserEntity(
            rs.getLong(COLUMN_USER_ID.getIndex()),
            rs.getString(COLUMN_EMAIL.getIndex()),
            rs.getString(COLUMN_NAME.getIndex()),
            rs.getString(COLUMN_PASSWORD.getIndex()),
            rs.getString(COLUMN_ROLE.getIndex())
        );
    }

    @Override
    public UserEntity create(String email, String name, String password, UserEntity.Role role) {
        long id = ghostFlowJdbcCrud.insertAndReturnKey(getValidEmail(email), password, name, role.name());
        return new UserEntity(id, email, name, password, role);
    }

    @Override
    public Optional<UserEntity> find(long id) {
        return ghostFlowJdbcCrud.selectById(rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<UserEntity> find(String email) {
        String sql = "SELECT " + columnsBuilder.getSelectClause() + " FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";
        return jdbcTemplate.query(sql, rowMapper, getValidEmail(email)).stream().findFirst();
    }

    @Override
    public Employees allEmployees(long limit, long offset) {
        String sql =
            "WITH selected_employees AS ( \n" +
            "   SELECT " + columnsBuilder.getSelectClause() + " \n" +
            "   FROM " + TABLE_NAME + " \n" +
            "   WHERE " + COLUMN_ROLE + " IN (:roles) OR " + COLUMN_ROLE + " IS NULL \n" +
            "   ORDER BY " + COLUMN_USER_ID + " DESC \n" +
            ") \n" +
            "SELECT count(1)::bigint, null::text, null::text, null::text, null::text, null::boolean \n" +
            "FROM selected_employees \n" +
            "UNION ALL \n" +
            "(SELECT * \n" +
            "FROM selected_employees \n" +
            "LIMIT :limit OFFSET :offset)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("roles", Lists.newArrayList(
            UserEntity.Role.INVESTIGATION.toString(),
            UserEntity.Role.OPERATIVE.toString(),
            UserEntity.Role.CHIEF_OPERATIVE.toString(),
            UserEntity.Role.RESEARCH_AND_DEVELOPMENT.toString()
        ));
        parameterSource.addValue("limit", limit);
        parameterSource.addValue("offset", offset);
        return namedParameterJdbcTemplate.query(sql, parameterSource, rs -> {
            rs.next();
            long count = rs.getLong(1);
            List<UserEntity> users = new ArrayList<>((int) Long.min(count, limit));
            while (rs.next()) {
                users.add(rowMapper.mapRow(rs, 0));
            }
            return new Employees(users, count);
        });
    }

    @Override
    public boolean authorize(String email, String password) {
        String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        return jdbcTemplate.query(sql, rowMapper, getValidEmail(email), password).stream().findAny().isPresent();
    }

    @Override
    public Optional<UserEntity> approve(long id, UserEntity.Role role) {
        String sql =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ROLE + " = ? WHERE " + COLUMN_USER_ID + " = ? \n" +
                "RETURNING " + columnsBuilder.getSelectClause();
        return jdbcTemplate.query(sql, rowMapper, role.name(), id).stream().findAny();
    }

    @Override
    public void delete(long id) {
        ghostFlowJdbcCrud.delete(id);
    }

    private static String getValidEmail(String email) {
        return email.toLowerCase().trim();
    }
}

