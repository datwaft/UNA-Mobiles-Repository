package org.una.server.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.FlightModel;

import java.sql.SQLException;

public class FlightController {
    private static FlightController instance = null;

    public JSONObject processQuery(JSONObject object) {
        try {
            return switch (object.getString("action")) {
                case "VIEW_ALL" -> viewAll();
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject viewAll() {
        try {
            var response = new JSONObject();
            response.put("action", "VIEW_ALL");
            response.put("view", FlightModel.getInstance().viewAll());
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
