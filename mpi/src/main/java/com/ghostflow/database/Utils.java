package com.ghostflow.database;

import com.ghostflow.GhostFlowException;

import java.sql.ResultSet;

public class Utils {
    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;

    public static <T> T getNullable(ResultSet resultSet, T obj) {
        try {
            return resultSet.wasNull() ? null : obj;
        } catch (Exception e) {
            throw new GhostFlowException(e);
        }
    }

    public static StringBuilder buildLikeQuery(String query) {
        StringBuilder builder = new StringBuilder(query.length());
        for (int i = 0; i < query.length(); i++) {
            char ch = Character.toLowerCase(query.charAt(i));
            switch (ch) {
                case '\\':
                case '_':
                case '%': {
                    builder.append('\\');
                }
                default: {
                    builder.append(ch);
                }
            }
        }
        return builder;
    }

    public static <T extends Enum<T>> T nullableValueOf(Class<T> enumType, String name) {
        if (name == null) {
            return null;
        } else {
            try {
                return T.valueOf(enumType, name.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
