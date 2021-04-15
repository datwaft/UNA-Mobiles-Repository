package org.una.server.controller;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.PlaneModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PlaneController {
    private static PlaneController instance = null;

    private static final PlaneModel model = PlaneModel.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    private final Set<Session> sessions = new HashSet<>();

    public void sendToMany(JSONObject message, Predicate<Session> condition) throws EncodeException, IOException {
        if (message == null) return;
        for (var session: sessions) {
            if (condition.test(session)) {
                session.getBasicRemote().sendObject(this.processQuery(message, session));
            }
        }
    }

    public void broadcast(JSONObject message) throws EncodeException, IOException {
        if (message == null) return;
        for (var session: sessions) {
            session.getBasicRemote().sendObject(this.processQuery(message, session));
        }
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
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
                    object.getString("name"),
                    object.getInt("type")
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
                    object.getString("name"),
                    object.getInt("type")
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

    public static PlaneController getInstance() {
        if (instance == null) instance = new PlaneController();
        return instance;
    }
}
