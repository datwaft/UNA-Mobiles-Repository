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
                case "GET" -> get(session);
                case "UPDATE" -> update(object, session);
                case "DEBUG" -> debug();
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    // TODO: delete this
    private JSONObject debug() {
        var response = new JSONObject();
        for (var session: sessionData.keySet()) {
            var sessionData = new JSONObject();
            sessionData.put("username", this.sessionData.get(session).getValue(0));
            sessionData.put("authorization", this.sessionData.get(session).getValue(1));
            response.put(session.getId(), sessionData);
        }
        return response;
    }

    public JSONObject checkCredentials(Session session) {
        if (!sessionData.containsKey(session)) {
            var response = new JSONObject();
            response.put("action", "ERROR");
            response.put("type", "CREDENTIALS");
            return response;
        } else {
            return null;
        }
    }

    public Boolean accountIsEqual(Session session1, Session session2) {
        return sessionData.get(session1).getValue0().equals(sessionData.get(session2).getValue0());
    }

    private JSONObject loginCaseAlreadyLoggedIn(Session session) {
        var response = new JSONObject();

        var username = sessionData.get(session).getValue(0);
        var authorization = sessionData.get(session).getValue(1);

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("authorization", authorization);
        return response;
    }

    private JSONObject loginCaseInvalid() {
        var response = new JSONObject();
        response.put("action", "ERROR");
        response.put("message", "Invalid credentials");
        return response;
    }

    private JSONObject loginCaseValid(String username, String authorization, Session session) {
        var response = new JSONObject();

        sessionData.put(session, Pair.with(username, authorization));

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("authorization", authorization);
        return response;
    }

    private JSONObject login(String username, String password, Session session) {
        try {
            // Case 1: Already logged in
            if (sessionData.containsKey(session)) return loginCaseAlreadyLoggedIn(session);
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

    private JSONObject register(JSONObject object) {
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
        if (!sessionData.containsKey(session)) return null;

        sessionData.remove(session);

        var response = new JSONObject();
        response.put("action", "LOGOUT");
        return response;
    }

    private JSONObject get(Session session) {
        JSONObject error;
        if ((error = checkCredentials(session)) != null) {
            return error;
        }
        var response = new JSONObject();
        try {
            var data = UserModel.getInstance().getUser(sessionData.get(session).getValue0());
            response.put("action", "GET");
            response.put("value", data);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        }
        return response;
    }

    private JSONObject update(JSONObject object, Session session) {
        JSONObject error;
        if ((error = checkCredentials(session)) != null) {
            return error;
        }
        var response = new JSONObject();
        try {
            UserModel.getInstance().updateUser(
                    sessionData.get(session).getValue0(),
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
