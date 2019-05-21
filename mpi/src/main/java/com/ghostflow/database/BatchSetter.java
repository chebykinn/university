package com.ghostflow.database;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import com.ghostflow.GhostFlowException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class BatchSetter<T> implements BatchPreparedStatementSetter {

    private final ArrayList<T> rows;
    private final ThrowableSetter<T> setter;

    public BatchSetter(ArrayList<T> rows, ThrowableSetter setter) {
        this.rows = rows;
        this.setter = setter;
    }

    public int getBatchSize() {
        return rows.size();
    }

    public void setValues(PreparedStatement ps, int index) throws SQLException {
        try {
            setter.accept(ps, index, rows);
        } catch (Exception e) {
            throw new GhostFlowException("unable to create prepared statement", e);
        }
    }

    @FunctionalInterface
    public interface ThrowableSetter<T> {
        void accept(PreparedStatement ps, int index, ArrayList<T> rows) throws Exception;
    }
}
