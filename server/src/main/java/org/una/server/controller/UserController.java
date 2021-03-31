package org.una.server.controller;

import jakarta.websocket.Session;
import org.javatuples.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.UserModel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    private static UserController instance = null;

    private final Map<Session, Pair<String, String>> sessionData = new HashMap<>();

    public JSONObject processQuery(JSONObject object, Session session) {
        try {
            return switch (object.getString("action")) {
                case "LOGIN" -> login(object.optString("username"), object.optString("password"), session);
                case "REGISTER" -> register(object);
                case "LOGOUT" -> logout(session);
                case "DEBUG" -> debug();
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    // TODO: delete this
    public JSONObject debug() {
        var response = new JSONObject();
        for (var session: sessionData.keySet()) {
            var sessionData = new JSONObject();
            sessionData.put("username", this.sessionData.get(session).getValue(0));
            sessionData.put("authorization", this.sessionData.get(session).getValue(1));
            response.put(session.getId(), sessionData);
        }
        return response;
    }

    public JSONObject loginCaseAlreadyLoggedIn(Session session) {
        var response = new JSONObject();

        var username = sessionData.get(session).getValue(0);
        var authorization = sessionData.get(session).getValue(1);

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("authorization", authorization);
        return response;
    }

    public JSONObject loginCaseInvalid() {
        var response = new JSONObject();
        response.put("action", "ERROR");
        response.put("message", "Invalid credentials");
        return response;
    }

    public JSONObject loginCaseValid(String username, String authorization, Session session) {
        var response = new JSONObject();

        sessionData.put(session, Pair.with(username, authorization));

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("authorization", authorization);
        return response;
    }

    public JSONObject login(String username, String password, Session session) {
        try {
            // Case 1: Already logged in
            if (sessionData.get(session) != null) return loginCaseAlreadyLoggedIn(session);
            // Case 2: Invalid credentials
            if (username == null || password == null) return loginCaseInvalid();
            var authorization = UserModel.getInstance().getAuthorization(username, password) ;
            if (authorization.equals("none")) return loginCaseInvalid();
            // Case 3: Valid credentials
            return loginCaseValid(username, authorization, session);
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
            return null;
        }
    }

    public JSONObject register(JSONObject object) {
        var response = new JSONObject();
        try {
            UserModel.getInstance().registerUser(
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

    public JSONObject logout(Session session) {
        if (sessionData.get(session) == null) return null;

        sessionData.remove(session);

        var response = new JSONObject();
        response.put("action", "LOGOUT");
        return response;
    }

    public static UserController getInstance() {
        if (instance == null) instance = new UserController();
        return instance;
    }
}
