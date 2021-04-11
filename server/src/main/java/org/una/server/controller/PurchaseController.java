package org.una.server.controller;

import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.PurchaseModel;

import java.sql.SQLException;

public class PurchaseController {
    private static PurchaseController instance = null;

    private static final PurchaseModel model = PurchaseModel.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "CREATE" -> create(object, session);
                case "VIEW_ALL" -> viewAll(object.optString("token"), session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject create(JSONObject object, Session session) {
        var response = new JSONObject();

        var token = object.optString("token");
        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (token == null || !sessionController.isTokenValid(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            model.create(
                    object.getInt("ticket_number"),
                    object.getInt("flight"),
                    sessionController.getUsernameByToken(token)
            );
            response.put("action", "CREATE");
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject viewAll(String token, Session session) {
        var response = new JSONObject();

        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (token == null || !sessionController.isTokenValid(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            var view = model.viewAll(sessionController.getUsernameByToken(token));
            response.put("action", "VIEW_ALL");
            response.put("view", view);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public static PurchaseController getInstance() {
        if (instance == null) instance = new PurchaseController();
        return instance;
    }
}
