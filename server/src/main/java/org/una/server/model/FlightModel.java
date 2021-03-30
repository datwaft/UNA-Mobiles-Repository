package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.FlightDBA;

import java.sql.SQLException;

public class FlightModel {
    private static FlightModel instance = null;

    private final FlightDBA dba;

    private FlightModel() {
        this.dba = FlightDBA.getInstance();
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public static FlightModel getInstance() {
        if (instance == null) instance = new FlightModel();
        return instance;
    }
}