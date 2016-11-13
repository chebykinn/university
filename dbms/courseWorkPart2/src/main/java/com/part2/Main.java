package com.part2;
import org.jooq.impl.DSL;
import classes.tables.Persons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.LogManager;

public class Main {
    static {
        LogManager.getLogManager().reset();
    }

    public static void main(String[] args) {
        String user = "coursework";
        String password = "123456789";
        String url = "jdbc:postgresql://chebykinn.ru:1488/coursework";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            DSL.using(connection)
                    .select(Persons.PERSONS.FIRST_NAME).from(Persons.PERSONS)
                    .forEach(
                            (l) -> System.out.println(l.get(0))
                    );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
