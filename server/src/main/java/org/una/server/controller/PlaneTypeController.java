package org.una.server.controller;

import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.PlaneTypeModel;

import java.sql.SQLException;

public class PlaneTypeController {
    private static final PlaneTypeModel model = PlaneTypeModel.getInstance();
    private static final SessionController sessionController = SessionController.getInstance();
    private static PlaneTypeController instance = null;

    public static PlaneTypeController getInstance() {
        if (instance == null) instance = new PlaneTypeController();
        return instance;
    }

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "GET_ALL" -> getAll(object.optString("token"), session);
                case "CREATE" -> create(object, session);
                case "UPDATE" -> update(object, session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject getAll(String token, Session session) {
        var response = new JSONObject();

        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (token == null || !sessionController.isTokenValid(token) || !sessionController.isTokenAdmin(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            var data = model.getAll();
            response.put("action", "GET_ALL");
            response.put("data", data);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject create(JSONObject object, Session session) {
        var response = new JSONObject();

        var token = object.optString("token");
        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (token == null || !sessionController.isTokenValid(token) || !sessionController.isTokenAdmin(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            model.create(
                    object.getInt("year"),
                    object.getString("model"),
                    object.getString("brand"),
                    object.getInt("rows"),
                    object.getInt("columns")
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

    public JSONObject update(JSONObject object, Session session) {
        var response = new JSONObject();

        var token = object.optString("token");
        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (token == null || !sessionController.isTokenValid(token) || !sessionController.isTokenAdmin(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            model.update(
                    object.getInt("identifier"),
                    object.getInt("year"),
                    object.getString("model"),
                    object.getString("brand"),
                    object.getInt("rows"),
                    object.getInt("columns")
            );
            response.put("action", "UPDATE");
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }
}
