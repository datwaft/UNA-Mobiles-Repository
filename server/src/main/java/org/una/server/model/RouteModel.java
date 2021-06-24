package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.RouteDBA;

import java.sql.SQLException;
import java.time.LocalTime;

public class RouteModel {
    private static RouteModel instance = null;

    private final RouteDBA dba;

    private RouteModel() {
        this.dba = RouteDBA.getInstance();
    }

    public static RouteModel getInstance() {
        if (instance == null) instance = new RouteModel();
        return instance;
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public void create(String origin, String destination, LocalTime duration, Integer price) throws SQLException {
        this.dba.create(origin, destination, duration, price);
    }

    public void update(Integer identifier, String origin, String destination, LocalTime duration, Integer price) throws SQLException {
        this.dba.update(identifier, origin, destination, duration, price);
    }
}
