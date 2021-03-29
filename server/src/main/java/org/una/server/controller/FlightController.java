package org.una.server.controller;

import org.json.JSONObject;
import org.una.server.model.FlightModel;

import java.sql.SQLException;

public class FlightController {
    private static FlightController instance = null;

    public JSONObject processQuery(JSONObject object) {
        return switch (object.getString("action")) {
            case "GET_ALL" -> getAll();
            default -> null;
        };
    }

    public JSONObject getAll() {
        try {
            var response = new JSONObject();
            response.put("action", "GET_ALL");
            response.put("value", FlightModel.getInstance().getAll());
            return response;
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
            return null;
        }
    }

    public static FlightController getInstance() {
        if (instance == null) instance = new FlightController();
        return instance;
    }
}
