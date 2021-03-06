package org.una.server.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.TicketModel;

import java.sql.SQLException;

public class TicketController {
    private static final TicketModel model = TicketModel.getInstance();
    private static TicketController instance = null;

    public static TicketController getInstance() {
        if (instance == null) instance = new TicketController();
        return instance;
    }

    public JSONObject processQuery(JSONObject object) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "VIEW_ALL_PER_FLIGHT" -> viewAllPerFlight(object);
                case "VIEW_ALL_PER_PURCHASE" -> viewAllPerPurchase(object);
                case "CREATE" -> create(object);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject viewAllPerFlight(JSONObject object) {
        var response = new JSONObject();
        try {
            var view = model.viewAllPerFlight(object.getInt("flight"));
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
            var view = model.viewAllPerPurchase(object.getInt("purchase"));
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

    public JSONObject create(JSONObject object) {
        var response = new JSONObject();
        var purchase = object.getInt("purchase");
        try {
            for (var _o : object.getJSONArray("tickets")) {
                var o = (JSONObject) _o;
                model.create(purchase, o.getInt("row"), o.getInt("column"));
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
}
