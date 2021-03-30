package org.una.server.controller;

import org.json.JSONObject;
import org.una.server.model.ScheduleModel;

import java.sql.SQLException;

public class ScheduleController {
    private static ScheduleController instance = null;

    public JSONObject processQuery(JSONObject object) {
        return switch (object.getString("action")) {
            case "GET_ALL_WITH_DISCOUNT" -> getAllWithDiscount();
            default -> null;
        };
    }

    public JSONObject getAllWithDiscount() {
        try {
            var response = new JSONObject();
            response.put("action", "GET_ALL_WITH_DISCOUNT");
            response.put("value", ScheduleModel.getInstance().getAllWithDiscount());
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
