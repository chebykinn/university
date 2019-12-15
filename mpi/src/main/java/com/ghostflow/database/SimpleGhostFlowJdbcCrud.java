package com.ghostflow.database;

import com.ghostflow.database.postgres.ColumnsBuilder.Column;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Streams;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ghostflow.database.postgres.ColumnsBuilder.COMMA_JOINER;

public class SimpleGhostFlowJdbcCrud<T> {

    private final JdbcTemplate jdbcTemplate;

    private final String insertSql;
    private final String insertSqlWithReturningClause;
    private final String selectByIdSql;
    private final String selectSql;
    private final String selectWithPaginationSql;
    private final String updateSql;
    private final String deleteSql;

    private final int idColumnsSize;
    private final long columnsSize;

    private SimpleGhostFlowJdbcCrud(JdbcTemplate jdbcTemplate, String tableName, List<Column> allColumns, List<Column> idColumns, List<Column> columns, List<Column> generatedColumns) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertSql = buildInsertSQL(tableName, columns);
        this.insertSqlWithReturningClause = this.insertSql + buildReturningSQL(allColumns);
        this.selectByIdSql = buildSelectByIdSQL(tableName, idColumns, allColumns);
        this.selectSql = buildSelectSQL(tableName, allColumns);
        this.selectWithPaginationSql = this.selectSql + " OFFSET ? LIMIT ?";
        this.updateSql = buildUpdateSQL(tableName, idColumns, columns);
        this.deleteSql = buildDeleteSql(tableName, idColumns);

        this.idColumnsSize = idColumns.size();
        this.columnsSize = columns.stream().filter(Column::isInsert).count();
    }

    private static String buildWhereClause(List<Column> idColumns) {
        return " WHERE " + idColumns.stream().map(c -> c.getName() + " = ?").collect(Collectors.joining(" AND "));
    }

    private static String buildInsertSQL(String tableName, List<Column> columns) {
        String intoClause = COMMA_JOINER.join(columns.stream().filter(Column::isInsert).map(Column::getName).collect(Collectors.toList()));
        String valuesClause = COMMA_JOINER.join(columns.stream().filter(Column::isInsert).map(Column::typed).collect(Collectors.toList()));
        return "INSERT INTO " + tableName + " ( " + intoClause + " ) VALUES ( " + valuesClause + " )";
    }

    private static String buildReturningSQL(List<Column> columns) {
        String returningClause = COMMA_JOINER.join(columns);
        return " RETURNING " + returningClause;
    }

    private static String buildSelectByIdSQL(String tableName, List<Column> idColumns, List<Column> allColumns) {
        String selectClause = COMMA_JOINER.join(allColumns);
        return "SELECT " + selectClause + " FROM " + tableName + buildWhereClause(idColumns);
    }

    private static String buildSelectSQL(String tableName, List<Column> allColumns) {
        String selectClause = COMMA_JOINER.join(allColumns);
        return "SELECT " + selectClause + " FROM " + tableName;
    }

    private static String buildUpdateSQL(String tableName, List<Column> idColumns, List<Column> columns) {
        String setClause = COMMA_JOINER.join(Streams.concat(columns.stream().filter(Column::isInsert).map(c -> c.getName() + " = " + c.typed())).collect(Collectors.toList()));
        return "UPDATE " + tableName + " SET " + setClause + buildWhereClause(idColumns);
    }

    private static String buildDeleteSql(String tableName, List<Column> idColumns) {
        return "DELETE FROM " + tableName + buildWhereClause(idColumns);
    }

    public void insert(Object... args) {
        Preconditions.checkArgument(columnsSize == args.length, "invalid args size");
        this.jdbcTemplate.update(insertSql, args);
    }

    public long insertAndReturnKey(Object... args) {
        Preconditions.checkArgument(idColumnsSize == 1, "must be one id column");
        Preconditions.checkArgument(columnsSize == args.length, "invalid args size");
        return this.jdbcTemplate.queryForObject(insertSqlWithReturningClause, Long.class, args);
    }

    public T insertAndReturn(RowMapper<T> rowMapper, Object... args) {
        Preconditions.checkArgument(columnsSize == args.length, "invalid args size");
        return this.jdbcTemplate.queryForObject(insertSqlWithReturningClause, rowMapper, args);
    }

    public List<T> selectById(RowMapper<T> rowMapper, Object... ids) {
        Preconditions.checkArgument(idColumnsSize == ids.length, "invalid ids size");
        return this.jdbcTemplate.query(selectByIdSql, rowMapper, ids);
    }

    public List<T> selectAll(RowMapper<T> rowMapper) {
        return this.jdbcTemplate.query(selectWithPaginationSql, rowMapper);
    }

    public List<T> selectAll(RowMapper<T> rowMapper, int offset, int limit) {
        return this.jdbcTemplate.query(selectWithPaginationSql, rowMapper, offset, limit);
    }

    public void update(Object id, Object... args) {
        Preconditions.checkArgument(idColumnsSize == 1, "invalid ids size");
        Preconditions.checkArgument(columnsSize == args.length, "invalid args size");
        this.jdbcTemplate.update(updateSql, ObjectArrays.concat(args, id));
    }

    public void update(Object[] ids, Object... args) {
        Preconditions.checkArgument(idColumnsSize == ids.length, "invalid ids size");
        Preconditions.checkArgument(columnsSize == args.length, "invalid args size");
        this.jdbcTemplate.update(updateSql, ObjectArrays.concat(args, ids, Object.class));
    }

    public void delete(Object... ids) {
        Preconditions.checkArgument(idColumnsSize == ids.length, "invalid ids size");
        this.jdbcTemplate.update(deleteSql, ids);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private String tableName;
        private List<Column> allColumns = new ArrayList<>();
        private List<Column> idColumns = new ArrayList<>();
        private List<Column> columns = new ArrayList<>();
        private List<Column> generatedColumns = new ArrayList<>();

        public Builder<T> withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder<T> withColumns(Column... column) {
            for (Column c : column) {
                withColumn(c);
            }
            return this;
        }

        public Builder<T> withIdColumns(Column... column) {
            for (Column c : column) {
                withIdColumn(c);
            }
            return this;
        }

        public Builder<T> withColumn(Column column) {
            allColumns.add(column);
            columns.add(column);
            return this;
        }

        public Builder<T> withIdColumn(Column idColumn) {
            allColumns.add(idColumn);
            generatedColumns.add(idColumn);
            idColumns.add(idColumn);
            return this;
        }

        public Builder<T> withGeneratedColumns(Column... generatedColumns) {
            this.allColumns.addAll(Arrays.asList(generatedColumns));
            this.generatedColumns.addAll(Arrays.asList(generatedColumns));
            return this;
        }

        public SimpleGhostFlowJdbcCrud<T> build(JdbcTemplate jdbcTemplate) {
            return new SimpleGhostFlowJdbcCrud<>(jdbcTemplate, tableName, allColumns, idColumns, columns, generatedColumns);
        }
    }
}
