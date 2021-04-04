package org.una.server.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.TicketModel;

import java.sql.SQLException;

public class TicketController {
    private static TicketController instance = null;

    public JSONObject processQuery(JSONObject object) {
        try {
            return switch (object.getString("action")) {
                case "CREATE" -> create(object);
                case "VIEW_ALL_PER_FLIGHT" -> viewAllPerFlight(object);
                case "VIEW_ALL_PER_PURCHASE" -> viewAllPerPurchase(object);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject create(JSONObject object) {
        var response = new JSONObject();
        var purchase = object.getInt("purchase");
        try {
            for (var _o : object.getJSONArray("tickets")) {
                var o = (JSONObject) _o;
                TicketModel.getInstance().create(purchase, o.getInt("row"), o.getInt("column"));
            }
            response.put("action", "CREATE");
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject viewAllPerFlight(JSONObject object) {
        var response = new JSONObject();
        try {
            var view = TicketModel.getInstance().viewAllPerFlight(object.getInt("flight"));
            response.put("action", "VIEW_ALL_PER_FLIGHT");
            response.put("flight", object.getInt("flight"));
            response.put("view", view);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject viewAllPerPurchase(JSONObject object) {
        var response = new JSONObject();
        try {
            var view = TicketModel.getInstance().viewAllPerPurchase(object.getInt("purchase"));
            response.put("action", "VIEW_ALL_PER_PURCHASE");
            response.put("purchase", object.getInt("purchase"));
            response.put("view", view);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public static TicketController getInstance() {
        if (instance == null) instance = new TicketController();
        return instance;
    }
}
