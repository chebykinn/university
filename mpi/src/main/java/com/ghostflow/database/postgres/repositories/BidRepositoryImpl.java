package com.ghostflow.database.postgres.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostflow.GhostFlowException;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.Bids;
import com.ghostflow.database.SimpleGhostFlowJdbcCrud;
import com.ghostflow.database.postgres.ColumnsBuilder;
import com.ghostflow.database.postgres.ColumnsBuilder.Column;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ghostflow.database.Utils.getNullable;

@Slf4j
@Repository("bidRepository")
public class BidRepositoryImpl implements BidRepository {
    private static final ColumnsBuilder columnsBuilder = new ColumnsBuilder();

    private static final int RETRIEVED_BIDS_LIMIT = 100;

    private static final String TABLE_NAME = "bids";

    private static final Column COLUMN_BID_ID       = columnsBuilder.newColumn("bid_id");
    private static final Column COLUMN_CUSTOMER_ID  = columnsBuilder.newColumn("customer_id");
    private static final Column COLUMN_EMPLOYEE_ID  = columnsBuilder.newColumn("employee_id");
    private static final Column COLUMN_STATE        = columnsBuilder.newColumn("state");
    private static final Column COLUMN_UPDATE_TIME  = columnsBuilder.newColumn("update_time");
    private static final Column COLUMN_DESCRIPTION  = columnsBuilder.newColumn("description", "jsonb");

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper objectMapper;
    private final SimpleGhostFlowJdbcCrud<BidEntity> ghostFlowJdbcCrud;
    private final RowMapper<BidEntity> rowMapper;

    private final Extended EXTENDED = new Extended();

    @Autowired
    public BidRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.objectMapper = objectMapper;
        this.ghostFlowJdbcCrud = SimpleGhostFlowJdbcCrud.<BidEntity>builder()
            .withTableName(TABLE_NAME)
            .withIdColumn(COLUMN_BID_ID)
            .withColumns(COLUMN_CUSTOMER_ID, COLUMN_EMPLOYEE_ID, COLUMN_STATE, COLUMN_UPDATE_TIME, COLUMN_DESCRIPTION)
            .build(jdbcTemplate);
        this.rowMapper = (rs, rowNum) -> new BidEntity(
            objectMapper,
                rs.getLong(COLUMN_BID_ID.getIndex()),
                getNullable(rs, rs.getLong(COLUMN_CUSTOMER_ID.getIndex())),
                getNullable(rs, rs.getLong(COLUMN_EMPLOYEE_ID.getIndex())),
                rs.getString(COLUMN_STATE.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex()).getTime(),
                rs.getString(COLUMN_DESCRIPTION.getIndex())
        );
    }

    @Override
    public long create(BidEntity entity) {
        return ghostFlowJdbcCrud.insertAndReturnKey(entity.getCustomerId(), entity.getEmployeeId(), entity.getStateStr(), entity.getUpdateTimestamp(), entity.getDescriptionStr(objectMapper));
    }

    public Optional<BidEntity> find(long id) {
        return ghostFlowJdbcCrud.selectById(rowMapper, id).stream().findAny();
    }

    @Override
    @Transactional
    public Optional<BidEntity> updateSafely(long id, Function<BidEntity, BidEntity> updater) {
        return find(id).map(e -> {
            BidEntity updated = updater.apply(e);
            ghostFlowJdbcCrud.update(updated.getBidId(), updated.getCustomerId(), updated.getEmployeeId(), updated.getUpdateTimestamp(), updated.getStateStr(), updated.getDescriptionStr(objectMapper));
            return updated;
        });
    }

    @Override
    @Transactional
    public boolean delete(long id, Predicate<BidEntity> checker) {
        return find(id).map(e -> {
            if (checker.test(e)) {
                ghostFlowJdbcCrud.delete(id);
                return true;
            } else {
                return false;
            }
        }).orElseThrow(() -> new GhostFlowException("Unknown bid id"));
    }

    @Override
    public BidRepository.Extended extended() {
        return EXTENDED;
    }

    private class Extended extends UserRepositoryImpl implements BidRepository.Extended {
        private final RowMapper<ExtendedBidEntity> rowMapper;

        public Extended() {
            super(jdbcTemplate, namedParameterJdbcTemplate);
            this.rowMapper = (rs, rowNum) -> new ExtendedBidEntity(
                objectMapper,
                rs.getLong(COLUMN_BID_ID.getIndex()),
                getNullable(rs, rs.getLong(COLUMN_CUSTOMER_ID.getIndex())),
                getNullable(rs, rs.getLong(COLUMN_EMPLOYEE_ID.getIndex())),
                rs.getString(COLUMN_STATE.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex()).getTime(),
                rs.getString(COLUMN_DESCRIPTION.getIndex()),
                rs.getString(COLUMN_DESCRIPTION.getIndex() + 1),
                rs.getString(COLUMN_DESCRIPTION.getIndex() + 2)
            );
        }

        private final String EXTENDED_SELECT =
            "SELECT " + BidRepositoryImpl.columnsBuilder.asStringStream().map(s -> "b." + s).collect(Collectors.joining(", ")) + ", c." + COLUMN_NAME + ", e." + COLUMN_NAME + "\n" +
            "FROM " + BidRepositoryImpl.TABLE_NAME + " AS b \n" +
            "LEFT JOIN " + UserRepositoryImpl.TABLE_NAME + " AS c ON(b." + COLUMN_CUSTOMER_ID + " = c." + COLUMN_USER_ID + ") \n" +
            "LEFT JOIN " + UserRepositoryImpl.TABLE_NAME + " AS e ON(b." + COLUMN_EMPLOYEE_ID + " = e." + COLUMN_USER_ID + ") \n";

        @Override
        public Optional<ExtendedBidEntity> findExtended(long id) {
            String sql =
                EXTENDED_SELECT +
                "WHERE b." + COLUMN_BID_ID + " = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findAny();
        }

        @Override
        public Bids findGtThanUpdateTimeExtended(long updateTime, String... typesStates) {
            String whereClause = typesStatesToWhere(typesStates);
            whereClause = whereClause.isEmpty() ? whereClause : whereClause + " AND ";
            String sql =
                EXTENDED_SELECT +
                "WHERE " + whereClause + COLUMN_UPDATE_TIME + " > ? \n" +
                "ORDER BY " + COLUMN_UPDATE_TIME + " ASC \n" +
                "LIMIT ?";
            List<Object> args = new ArrayList<>(typesStates.length + 2);
            Collections.addAll(args, typesStates);
            args.add(Timestamp.from(Instant.ofEpochMilli(updateTime)));
            args.add(RETRIEVED_BIDS_LIMIT);
            return new Bids(jdbcTemplate.query(sql, rowMapper, args.toArray()), updateTime);
        }

        @Override
        public List<ExtendedBidEntity> findExtended(long ownerId, long limit, long offset) {
            String sql =
                EXTENDED_SELECT +
                "WHERE b." + COLUMN_CUSTOMER_ID + " = ? \n" +
                "ORDER BY b." + COLUMN_UPDATE_TIME + " ASC \n" +
                "LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, rowMapper, ownerId, limit, offset);
        }

        @Override
        public Bids findByEmployeeExtended(long employeeId, long limit, long offset) {
            String sql =
                "WITH selected_bids AS ( \n" +
                "   " + EXTENDED_SELECT +
                "   WHERE b." + COLUMN_EMPLOYEE_ID + " = ? \n" +
                ") \n" +
                "SELECT count(1)::bigint, null::bigint, null::bigint, null::text, null::timestamp, null::jsonb, null::text, null::text \n" +
                "FROM selected_bids \n" +
                "UNION ALL \n" +
                "(SELECT * \n" +
                "FROM selected_bids \n" +
                "ORDER BY " + COLUMN_UPDATE_TIME + " ASC \n" +
                "LIMIT ? OFFSET ?)";
            return jdbcTemplate.query(sql, rs -> { return rsToBids(rs, limit); }, employeeId, limit, offset);
        }

        @Override
        public Bids findExtended(long limit, long offset, String ... typesStates) {
            String whereClause = typesStatesToWhere(typesStates);
            String sql =
                "WITH selected_bids AS ( \n" +
                "   " + EXTENDED_SELECT +
                "   WHERE " + whereClause + " \n" +
                ") \n" +
                "SELECT count(1)::bigint, null::bigint, null::bigint, null::text, coalesce(max(" + COLUMN_UPDATE_TIME + "), to_timestamp(0)), null::jsonb, null::text, null::text \n" +
                "FROM selected_bids \n" +
                "UNION ALL \n" +
                "(SELECT * \n" +
                "FROM selected_bids \n" +
                "ORDER BY " + COLUMN_UPDATE_TIME + " ASC \n" +
                "LIMIT ? OFFSET ?)";
            List<Object> args = new ArrayList<>(typesStates.length + 2);
            Collections.addAll(args, typesStates);
            args.add(limit);
            args.add(offset);
            return jdbcTemplate.query(sql, args.toArray(), rs -> { return rsToBids(rs, limit); });
        }

        private String typesStatesToWhere(String... typesStates) {
            return IntStream.range(0, typesStates.length / 2)
                .mapToObj(i -> "(b." + COLUMN_DESCRIPTION + "->>'type' = ? AND b." + COLUMN_STATE + " = ?)")
                .collect(Collectors.joining(" OR "));
        }

        private Bids rsToBids(ResultSet rs, long limit) throws SQLException {
            rs.next();
            long count = rs.getLong(1);
            Timestamp lastUpdateTime = rs.getTimestamp(5);
            List<ExtendedBidEntity> bids = new ArrayList<>((int) Long.min(count, limit));
            while (rs.next()) {
                bids.add(rowMapper.mapRow(rs, 0));
            }
            return new Bids(bids, count, lastUpdateTime);
        }

    }
}

