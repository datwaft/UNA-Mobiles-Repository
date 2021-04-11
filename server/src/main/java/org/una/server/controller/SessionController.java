package org.una.server.controller;

import jakarta.websocket.Session;
import org.javatuples.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.UserModel;
import org.una.server.util.TokenUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionController {
    private static SessionController instance = null;

    private static final UserModel model = UserModel.getInstance();

    private Map<Session, String> sessionTokens = new HashMap<>();
    private final Map<String, Pair<String, String>> tokenData = new HashMap<>();

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "LOGIN" -> login(object.optString("username"), object.optString("password"), session);
                case "LOGOUT" -> logout(session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public void registerSession(Session session, String token) {
        if (!isSessionSignedIn(session)) {
            sessionTokens.put(session, token);
        } else if (!sessionTokens.get(session).equals(token)) {
            sessionTokens.remove(session);
            sessionTokens.put(session, token);
        }
    }

    public Boolean shareUsername(Session session1, Session session2) {
        if (isSessionSignedIn(session1) && isSessionSignedIn(session2)) {
            return getUsernameBySession(session1).equals(getUsernameBySession(session2));
        }
        return false;
    }

    public Boolean isTokenValid(String token) {
        return tokenData.containsKey(token);
    }

    public Boolean isSessionSignedIn(Session session) {
        return sessionTokens.containsKey(session);
    }

    public Boolean isTokenAdmin(String token) {
        return tokenData.get(token).getValue1().equals("admin");
    }

    public Boolean isSessionAdmin(Session session) {
        var token = sessionTokens.get(session);
        return tokenData.get(token).getValue1().equals("admin");
    }

    public Boolean isTokenUser(String token) {
        return tokenData.get(token).getValue1().equals("user");
    }

    public Boolean isSessionUser(Session session) {
        var token = sessionTokens.get(session);
        return tokenData.get(token).getValue1().equals("user");
    }

    public String getUsernameByToken(String token) {
        return tokenData.get(token).getValue0();
    }

    public String getTokenBySession(Session session) {
        return sessionTokens.get(session);
    }

    public String getUsernameBySession(Session session) {
        return getSessionData(session).getValue0();
    }

    private Pair<String, String> getSessionData(Session session) {
        var token = sessionTokens.get(session);
        return tokenData.get(token);
    }

    private JSONObject loginCaseAlreadyLoggedIn(Session session) {
        var response = new JSONObject();

        var token = sessionTokens.get(session);
        var username = getSessionData(session).getValue(0);
        var authorization = getSessionData(session).getValue(1);

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("token", token);
        response.put("authorization", authorization);
        return response;
    }

    private JSONObject loginCaseInvalid() {
        var response = new JSONObject();
        response.put("action", "ERROR");
        response.put("type", "CREDENTIALS");
        return response;
    }

    private JSONObject loginCaseValid(String username, String authorization, Session session) {
        var response = new JSONObject();

        var token = TokenUtils.generateToken();
        sessionTokens.put(session, token);
        tokenData.put(token, Pair.with(username, authorization));

        response.put("action", "LOGIN");
        response.put("username", username);
        response.put("token", token);
        response.put("authorization", authorization);
        return response;
    }

    private JSONObject login(String username, String password, Session session) {
        try {
            // Case 1: Already logged in
            if (isSessionSignedIn(session)) return loginCaseAlreadyLoggedIn(session);
            // Case 2: Invalid credentials
            if (username == null || password == null) return loginCaseInvalid();
            var authorization = model.getAuthorization(username, password) ;
            if (authorization.equals("none")) return loginCaseInvalid();
            // Case 3: Valid credentials
            return loginCaseValid(username, authorization, session);
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
            return null;
        }
    }
    public JSONObject logout(Session session) {
        if (!sessionTokens.containsKey(session)) return null;

        var token = sessionTokens.get(session);
        this.sessionTokens = sessionTokens.entrySet().stream()
                .filter((e) -> !e.getValue().equals(token))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        tokenData.remove(token);

        var response = new JSONObject();
        response.put("action", "LOGOUT");
        return response;
    }

    public static SessionController getInstance() {
        if (instance == null) instance = new SessionController();
        return instance;
    }
}
