package com.ghostflow.database.postgres.repositories;

import com.ghostflow.database.CompanyReviewRepository;
import com.ghostflow.database.CompanyReviews;
import com.ghostflow.database.SimpleGhostFlowJdbcCrud;
import com.ghostflow.database.postgres.ColumnsBuilder;
import com.ghostflow.database.postgres.ColumnsBuilder.Column;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.database.postgres.entities.ExtendedCompanyReviewEntity;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository("companyReviewRepository")
public class CompanyReviewRepositoryImpl implements CompanyReviewRepository {
    private static final ColumnsBuilder columnsBuilder;

    public static final String TABLE_NAME;

    public static final Column COLUMN_COMPANY_REVIEW_ID;
    private static final Column COLUMN_BID_ID;
    private static final Column COLUMN_RATING;
    private static final Column COLUMN_REVIEW;
    private static final Column COLUMN_UPDATE_TIME;

    static {
        columnsBuilder = new ColumnsBuilder();
        TABLE_NAME = "company_reviews";

        COLUMN_COMPANY_REVIEW_ID    = columnsBuilder.newColumn("company_review_id");
        COLUMN_BID_ID               = columnsBuilder.newColumn("bid_id");
        COLUMN_RATING               = columnsBuilder.newColumn("rating");
        COLUMN_REVIEW               = columnsBuilder.newColumn("review");
        COLUMN_UPDATE_TIME          = columnsBuilder.newColumn("update_time");
    }

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleGhostFlowJdbcCrud<CompanyReviewEntity> ghostFlowJdbcCrud;
    private final RowMapper<CompanyReviewEntity> rowMapper;

    private final Extended EXTENDED = new Extended();

    @Autowired
    public CompanyReviewRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.ghostFlowJdbcCrud = SimpleGhostFlowJdbcCrud.<CompanyReviewEntity>builder()
            .withTableName(TABLE_NAME)
            .withIdColumn(COLUMN_COMPANY_REVIEW_ID)
            .withColumns(COLUMN_BID_ID, COLUMN_RATING, COLUMN_REVIEW, COLUMN_UPDATE_TIME)
            .build(jdbcTemplate);
        this.rowMapper = (rs, rowNum) -> new CompanyReviewEntity(
                rs.getLong(COLUMN_COMPANY_REVIEW_ID.getIndex()),
                rs.getLong(COLUMN_BID_ID.getIndex()),
                rs.getInt(COLUMN_RATING.getIndex()),
                rs.getString(COLUMN_REVIEW.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex())
            );
    }

    @Override
    public CompanyReviewEntity create(CompanyReviewEntity entity) {
        return ghostFlowJdbcCrud.insertAndReturn(rowMapper, entity.getBidId(), entity.getRating(), entity.getReview(), PGTimestamp.from(Instant.now(Clock.systemUTC())));
    }

    @Override
    public Optional<CompanyReviewEntity> find(long id) {
        return ghostFlowJdbcCrud.selectById(rowMapper, id).stream().findAny();
    }

    @Override
    public Optional<CompanyReviewEntity> findByBidId(long bidId) {
        String sql = "SELECT " + columnsBuilder.getSelectClause() + " FROM " + TABLE_NAME + " WHERE " + COLUMN_BID_ID + " = ?";
        return jdbcTemplate.query(sql, rowMapper, bidId).stream().findFirst();
    }

    @Override
    public Optional<CompanyReviewEntity> update(long companyReviewId, int rating, String review) {
        String setClause = Arrays.asList(COLUMN_RATING, COLUMN_REVIEW, COLUMN_UPDATE_TIME).stream()
            .map(s -> s + " = ?")
            .collect(Collectors.joining(", "));

        String sql =
            "UPDATE " + TABLE_NAME + " SET " + setClause + " WHERE " + COLUMN_COMPANY_REVIEW_ID + " = ? \n" +
            "RETURNING " + columnsBuilder.getSelectClause();
        return jdbcTemplate.query(sql, rowMapper, rating, review, PGTimestamp.from(Instant.now(Clock.systemUTC())), companyReviewId).stream().findAny();
    }

    @Override
    public void delete(long id) {
        ghostFlowJdbcCrud.delete(id);
    }

    @Override
    public CompanyReviewRepository.Extended extended() {
        return EXTENDED;
    }

    private class Extended extends UserRepositoryImpl implements CompanyReviewRepository.Extended {
        private final RowMapper<ExtendedCompanyReviewEntity> rowMapper;

        public Extended() {
            super(jdbcTemplate, namedParameterJdbcTemplate);
            this.rowMapper = (rs, rowNum) -> new ExtendedCompanyReviewEntity(
                rs.getLong(CompanyReviewRepositoryImpl.COLUMN_COMPANY_REVIEW_ID.getIndex()),
                rs.getLong(CompanyReviewRepositoryImpl.COLUMN_BID_ID.getIndex()),
                rs.getInt(CompanyReviewRepositoryImpl.COLUMN_RATING.getIndex()),
                rs.getString(CompanyReviewRepositoryImpl.COLUMN_REVIEW.getIndex()),
                rs.getTimestamp(COLUMN_UPDATE_TIME.getIndex()),
                rs.getString(COLUMN_UPDATE_TIME.getIndex() + 1)
            );
        }

        private final String EXTENDED_SELECT;
        {
            EXTENDED_SELECT = "SELECT " + CompanyReviewRepositoryImpl.columnsBuilder.asStringStream().map(s -> "c." + s).collect(Collectors.joining(", ")) + ", u." + COLUMN_NAME + "\n" +
                "FROM " + CompanyReviewRepositoryImpl.TABLE_NAME + " AS c \n" +
                "LEFT JOIN " + BidRepositoryImpl.TABLE_NAME + " AS b USING (" + COLUMN_BID_ID + ") \n" +
                "LEFT JOIN " + UserRepositoryImpl.TABLE_NAME + " AS u ON (" + BidRepositoryImpl.COLUMN_CUSTOMER_ID + " = " + UserRepositoryImpl.COLUMN_USER_ID + ") \n";
        }

        @Override
        public Optional<ExtendedCompanyReviewEntity> findExtended(long id) {
            String sql =
                EXTENDED_SELECT +
                "WHERE c." + CompanyReviewRepositoryImpl.COLUMN_COMPANY_REVIEW_ID + " = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findAny();
        }

        @Override
        public CompanyReviews all(int offset, int limit) {
            String sql =
                "WITH selected_reviews AS (\n" +
                "   " + EXTENDED_SELECT +
                "   ORDER BY " + COLUMN_UPDATE_TIME + " DESC \n" +
                ") \n" +
                "SELECT count(1)::bigint, null::bigint, null::int, null::text, null::timestamp, null::text \n" +
                "FROM selected_reviews \n" +
                "UNION ALL \n" +
                "(SELECT * \n" +
                "FROM selected_reviews \n" +
                "LIMIT ? OFFSET ?)";
            return jdbcTemplate.query(sql, rs -> {
                rs.next();
                long count = rs.getLong(1);
                List<ExtendedCompanyReviewEntity> reviews = new ArrayList<>((int) Long.min(count, limit));
                while (rs.next()) {
                    reviews.add(rowMapper.mapRow(rs, 0));
                }
                return new CompanyReviews(reviews, count);
            }, limit, offset);
        }
    }
}

