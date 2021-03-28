package org.una.server.data;

import java.sql.Connection;
import java.sql.DriverManager;

import static java.lang.System.exit;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;

    private Connection connection;

    private DatabaseConnection() {
        String password = "losiram01";
        String user = "postgres";
        String url = "jdbc:postgresql://localhost/airline";
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            System.err.println("Couldn't connect to database");
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            exit(0);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }
}