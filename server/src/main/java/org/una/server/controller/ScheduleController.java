package org.una.server.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.ScheduleModel;

import java.sql.SQLException;

public class ScheduleController {
    private static ScheduleController instance = null;

    public JSONObject processQuery(JSONObject object) {
        try {
            return switch (object.getString("action")) {
                case "VIEW_ALL_WITH_DISCOUNT" -> viewAllWithDiscount();
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject viewAllWithDiscount() {
        try {
            var response = new JSONObject();
            response.put("action", "VIEW_ALL_WITH_DISCOUNT");
            response.put("view", ScheduleModel.getInstance().viewAllWithDiscount());
            return response;
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
            return null;
        }
    }

    public static ScheduleController getInstance() {
        if (instance == null) instance = new ScheduleController();
        return instance;
    }
}
