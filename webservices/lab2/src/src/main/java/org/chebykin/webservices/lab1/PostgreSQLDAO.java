package org.chebykin.webservices.lab1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLDAO {
    private final Connection connection;


    public PostgreSQLDAO(Connection connection) {
        this.connection = connection;
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

    public Connection getConnection() {
        return connection;
    }
}
