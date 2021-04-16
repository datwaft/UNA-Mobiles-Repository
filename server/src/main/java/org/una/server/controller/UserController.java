package org.una.server.controller;

import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.UserModel;

import java.sql.SQLException;

public class UserController {
    private static UserController instance = null;

    private static final UserModel model = UserModel.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "GET" -> get(object.optString("token"), session);
                case "REGISTER" -> register(object);
                case "UPDATE" -> update(object, session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    private JSONObject get(String token, Session session) {
        var response = new JSONObject();

        if ((token == null || token.isEmpty()) && sessionController.isSessionSignedIn(session)) {
            token = sessionController.getTokenBySession(session);
        }
        if (!sessionController.isTokenValid(token)) {
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            sessionController.registerSession(session, token);
        }

        try {
            var username = sessionController.getUsernameByToken(token);
            var data = model.get(username);
            response.put("action", "GET");
            response.put("get", data);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        }
        return response;
    }

    private JSONObject register(JSONObject object) {
        var response = new JSONObject();
        try {
            UserModel.getInstance().register(
                    object.getString("username"),
                    object.getString("password"),
                    object.getString("name"),
                    object.getString("lastname"),
                    object.getString("email"),
                    object.getString("address"),
                    object.getString("workphone"),
                    object.getString("mobilephone")
            );
            response.put("action", "REGISTER");
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("type", "DUPLICATE");
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    private JSONObject update(JSONObject object, Session session) {
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
            model.update(
                    sessionController.getUsernameByToken(token),
                    object.getString("name"),
                    object.getString("lastname"),
                    object.getString("email"),
                    object.getString("address"),
                    object.getString("workphone"),
                    object.getString("mobilephone")
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

    public static UserController getInstance() {
        if (instance == null) instance = new UserController();
        return instance;
    }
}
