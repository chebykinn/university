package org.chebykin.webservices.lab1;

import javax.swing.plaf.nimbus.State;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLDAO {
    private final Connection connection;
    private long personsCount = 0;

    enum ColumnType {
        String,
        Int
    };
    class Column {
        String name;
        ColumnType type;

        public Column(String name, ColumnType type) {
            this.name = name;
            this.type = type;
        }
    }

    private HashMap<String,Column> columns;


    public PostgreSQLDAO(Connection connection) throws SQLException {
        this.connection = connection;
        ResultSet rs = connection.createStatement().executeQuery("select count(*) from persons");
        if(!rs.next()) {
            throw new RuntimeException("Failed to get persons count");
        }
        this.personsCount = rs.getLong(1);
        columns = new HashMap<>();
        columns.put("name", new Column("name", ColumnType.String));
        columns.put("surname", new Column("surname", ColumnType.String));
        columns.put("job", new Column("job", ColumnType.String));
        columns.put("city", new Column("city", ColumnType.String));
        columns.put("age", new Column("age", ColumnType.Int));
    }

    public List<Person> getPersons() {
        List<Person> persons = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from persons");
            while (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String job = rs.getString("job");
                String city = rs.getString("city");
                int age = rs.getInt("age");
                Person person = new Person(name, surname, job, city, age);
                persons.add(person);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return persons;
    }

    private int setStatementFromMap(int startIndex, PreparedStatement stmt, HashMap<String, String> fields) throws SQLException {
        int fieldIndex = startIndex;
        for(HashMap.Entry<String, String> ent : fields.entrySet()) {
            Column c = columns.get(ent.getKey());
            if(c.type == ColumnType.Int) {
                int val = Integer.parseInt(ent.getValue());
                stmt.setInt(fieldIndex++, val);
                continue;
            }
            stmt.setString(fieldIndex++, ent.getValue());
        }
        return fieldIndex;
    }

    public long addPerson(HashMap<String, String> fields) {
        try {
            StringBuilder questions = new StringBuilder("?");
            for(int i = 0; i < fields.size(); i++) {
                questions.append(",?");
            }
            for (String column : fields.keySet()) {
                if(!columns.containsKey(column)) {
                    throw new RuntimeException("No such column: " + column);
                }
            }
            String keys = String.join("\",\"", fields.keySet());
            String queryStr = "INSERT INTO persons (\"id\",\"" + keys + "\") VALUES(" + questions + ")";
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, queryStr);
            PreparedStatement stmt = connection.prepareStatement(queryStr);
            stmt.setLong(1, personsCount + 1);
            setStatementFromMap(2, stmt, fields);

            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, "sql: " + stmt);
            int rows = stmt.executeUpdate();
            if(rows <= 0) {
                return -1;
            }
            personsCount++;
            return personsCount;
        } catch (SQLException ex) {
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updatePerson(int id, HashMap<String, String> fields) {
        if(id < 0) return false;
        try {
            for (String column : fields.keySet()) {
                if(!columns.containsKey(column)) {
                    throw new RuntimeException("No such column: " + column);
                }
            }
            int i = 0;
            StringBuilder questions = new StringBuilder();
            for(HashMap.Entry<String, String> ent : fields.entrySet()) {
                questions.append("SET ").append(ent.getKey());
                if(i++ == fields.size() - 1) {
                    questions.append(" = ?");
                } else {
                    questions.append(" = ?, ");
                }
            }
            String queryStr = "UPDATE persons " + questions + " WHERE id = ?";
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, queryStr);
            PreparedStatement stmt = connection.prepareStatement(queryStr);
            int fieldIndex = setStatementFromMap(1, stmt, fields);
            stmt.setInt(fieldIndex, id);

            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, "sql: " + stmt);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    boolean deletePerson(int id) {
        if(id < 0) return false;
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM persons WHERE id = ?");
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public boolean uploadAvatar(int id, byte[] image) {
        if(id < 0) return false;
        try {
            String queryStr = "UPDATE persons SET avatar = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(queryStr);
            ByteArrayInputStream bs = new ByteArrayInputStream(image);
            stmt.setBinaryStream(1, bs, image.length);
            stmt.setInt(2, id);
            bs.close();

            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, "sql: " + stmt);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException | IOException ex) {
            Logger.getLogger(PostgreSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    public Connection getConnection() {
        return connection;
    }
}
