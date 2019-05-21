package com.ghostflow.database.postgres;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColumnsBuilder {
    public static final Joiner COMMA_JOINER = Joiner.on(", ");

    private int index = 0;
    private ArrayList<Column> columns = new ArrayList<>();

    public Column newColumn(String name) {
        Column column = new Column(++index, name);
        columns.add(column);
        return column;
    }

    public Column newColumn(String name, String type) {
        Column column = new Column(++index, name, type);
        columns.add(column);
        return column;
    }

    public Stream<Column> asStream() {
        return columns.stream();
    }

    public Stream<String> asStringStream() {
        return columns.stream().map(Column::toString);
    }

    public String getSelectClause() {
        return COMMA_JOINER.join(columns);
    }

    public String asJdbcTemplateInsertValues() {
        return COMMA_JOINER.join(columns.stream().map(Column::typed).collect(Collectors.toList()));
    }

    public int count() {
        return columns.size();
    }

    public class Column {
        private final int index;
        private final String name;
        private final String typeName;

        public Column(int index, String name, String typeName) {
            this.index = index;
            this.name = name;
            this.typeName = typeName;
        }

        private Column(int index, String name) {
            this(index, name, null);
        }

        private Column() {
            this(0, null);
        }

        public int getIndex() {
            return index;
        }

        public String typed() {
            return "?" + (typeName == null ? "" : "::" + typeName);
        }

        public String getName() {
            return name;
        }

        public String getTypeName() {
            return typeName;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
