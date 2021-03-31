package org.una.server.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static java.lang.System.exit;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;

    private Connection connection;

    private DatabaseConnection() {
        String user = "postgres";
        String password = "losiram01";
        String url = "jdbc:postgresql://localhost/airline";
        try {
            Class.forName("org.postgresql.Driver");
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
