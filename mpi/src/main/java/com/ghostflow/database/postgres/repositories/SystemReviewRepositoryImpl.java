package com.ghostflow.database.postgres.repositories;

import com.ghostflow.database.SimpleGhostFlowJdbcCrud;
import com.ghostflow.database.SystemReviewRepository;
import com.ghostflow.database.postgres.ColumnsBuilder;
import com.ghostflow.database.postgres.ColumnsBuilder.Column;
import com.ghostflow.database.postgres.entities.ExtendedSystemReviewEntity;
import com.ghostflow.database.postgres.entities.SystemReviewEntity;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository("systemReviewRepository")
public class SystemReviewRepositoryImpl implements SystemReviewRepository {
    private static final ColumnsBuilder columnsBuilder;

    private static final String TABLE_NAME;

    private static final Column COLUMN_SYSTEM_REVIEW_ID;
    private static final Column COLUMN_USER_ID;
    private static final Column COLUMN_RATING;
    private static final Column COLUMN_REVIEW;
    private static final Column COLUMN_UPDATE_TIME;

    static {
        columnsBuilder = new ColumnsBuilder();
        TABLE_NAME = "system_reviews";

        COLUMN_SYSTEM_REVIEW_ID = columnsBuilder.newColumn("system_review_id");
        COLUMN_USER_ID          = columnsBuilder.newColumn("user_id");
        COLUMN_RATING           = columnsBuilder.newColumn("rating");
        COLUMN_REVIEW           = columnsBuilder.newColumn("review");
        COLUMN_UPDATE_TIME      = columnsBuilder.newColumn("update_time");
    }

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleGhostFlowJdbcCrud<SystemReviewEntity> ghostFlowJdbcCrud;
    private final RowMapper<SystemReviewEntity> rowMapper;

    private final Extended EXTENDED = new Extended();

    @Autowired
    public SystemReviewRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.ghostFlowJdbcCrud = SimpleGhostFlowJdbcCrud.<SystemReviewEntity>builder()
            .withTableName(TABLE_NAME)
            .withIdColumn(COLUMN_SYSTEM_REVIEW_ID)
            .withColumns(COLUMN_USER_ID, COLUMN_RATING, COLUMN_REVIEW, COLUMN_UPDATE_TIME)
            .build(jdbcTemplate);
        this.rowMapper = (rs, rowNum) -> new SystemReviewEntity(
                rs.getLong(COLUMN_SYSTEM_REVIEW_ID.getIndex()),
                rs.getLong(COLUMN_USER_ID.getIndex()),
                rs.getInt(COLUMN_RATING.getIndex()),
                rs.getString(COLUMN_REVIEW.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex())
            );
    }

    @Override
    public SystemReviewEntity create(SystemReviewEntity entity) {
        return ghostFlowJdbcCrud.insertAndReturn(rowMapper, entity.getUserId(), entity.getRating(), entity.getReview(), PGTimestamp.from(Instant.now(Clock.systemUTC())));
    }

    @Override
    public Optional<SystemReviewEntity> find(long id) {
        return ghostFlowJdbcCrud.selectById(rowMapper, id).stream().findAny();
    }

    @Override
    public Optional<SystemReviewEntity> findByUserId(long userId) {
        String sql = "SELECT " + columnsBuilder.getSelectClause() + " FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";
        return jdbcTemplate.query(sql, rowMapper, userId).stream().findFirst();
    }

    @Override
    public Optional<SystemReviewEntity> update(long systemReviewId, int rating, String review) {
        String setClause = Arrays.asList(COLUMN_RATING, COLUMN_REVIEW, COLUMN_UPDATE_TIME).stream()
            .map(s -> s + " = ?")
            .collect(Collectors.joining(", "));

        String sql =
            "UPDATE " + TABLE_NAME + " SET " + setClause + " WHERE " + COLUMN_SYSTEM_REVIEW_ID + " = ? \n" +
            "RETURNING " + columnsBuilder.getSelectClause();
        return jdbcTemplate.query(sql, rowMapper, rating, review, PGTimestamp.from(Instant.now(Clock.systemUTC())), systemReviewId).stream().findAny();
    }

    @Override
    public void delete(long id) {
        ghostFlowJdbcCrud.delete(id);
    }

    @Override
    public SystemReviewRepository.Extended extended() {
        return EXTENDED;
    }

    private class Extended extends UserRepositoryImpl implements SystemReviewRepository.Extended {
        private final RowMapper<ExtendedSystemReviewEntity> rowMapper;

        public Extended() {
            super(jdbcTemplate, namedParameterJdbcTemplate);
            this.rowMapper = (rs, rowNum) -> new ExtendedSystemReviewEntity(
                rs.getLong(SystemReviewRepositoryImpl.COLUMN_SYSTEM_REVIEW_ID.getIndex()),
                rs.getLong(SystemReviewRepositoryImpl.COLUMN_USER_ID.getIndex()),
                rs.getInt(SystemReviewRepositoryImpl.COLUMN_RATING.getIndex()),
                rs.getString(SystemReviewRepositoryImpl.COLUMN_REVIEW.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex()),
                rs.getString(COLUMN_UPDATE_TIME.getIndex() + 1),
                rs.getString(COLUMN_UPDATE_TIME.getIndex() + 2)
            );
        }

        private final String EXTENDED_SELECT;

        {
            EXTENDED_SELECT =
                "SELECT " + SystemReviewRepositoryImpl.columnsBuilder.asStringStream().map(s -> "r." + s).collect(Collectors.joining(", ")) + ", u." + COLUMN_ROLE + ", u." + COLUMN_NAME + "\n" +
                "FROM " + SystemReviewRepositoryImpl.TABLE_NAME + " AS r \n" +
                "LEFT JOIN " + UserRepositoryImpl.TABLE_NAME + " AS u USING (" + COLUMN_USER_ID + ") \n";
        }

        @Override
        public Optional<ExtendedSystemReviewEntity> findExtended(long id) {
            String sql =
                EXTENDED_SELECT +
                "WHERE r." + SystemReviewRepositoryImpl.COLUMN_SYSTEM_REVIEW_ID + " = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findAny();
        }

        @Override
        public List<ExtendedSystemReviewEntity> all(int offset, int limit) {
            String sql =
                EXTENDED_SELECT +
                "ORDER BY " + COLUMN_UPDATE_TIME + " DESC \n" +
                "LIMIT ? \n" +
                "OFFSET ?";
            return jdbcTemplate.query(sql, rowMapper, limit, offset);
        }
    }
}

