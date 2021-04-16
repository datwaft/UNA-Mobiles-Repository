package org.una.server.controller;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.FlightModel;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlightController {
    private static FlightController instance = null;

    private static final FlightModel model = FlightModel.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "VIEW_ALL" -> viewAll();
                case "GET_ALL" -> getAll(object.optString("token"), session);
                case "CREATE" -> create(object, session);
                case "UPDATE" -> update(object, session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject viewAll() {
        try {
            var response = new JSONObject();
            response.put("action", "VIEW_ALL");
            response.put("view", model.viewAll());
            return response;
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
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
                    object.getInt("plane"),
                    LocalDate.parse(object.getString("outbound_date")),
                    object.getInt("outbound_schedule"),
                    object.optString("inbound_date").isEmpty() ?
                            null :
                            LocalDate.parse(object.getString("inbound_date")),
                    object.optInt("inbound_schedule") == 0 ? null : object.getInt("inbound_schedule")
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
                    object.getInt("plane"),
                    LocalDate.parse(object.getString("outbound_date")),
                    object.getInt("outbound_schedule"),
                    object.optString("inbound_date").isEmpty() ?
                            null :
                            LocalDate.parse(object.getString("inbound_date")),
                    object.optInt("inbound_schedule") == 0 ? null : object.getInt("inbound_schedule")
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

    public static FlightController getInstance() {
        if (instance == null) instance = new FlightController();
        return instance;
    }
}
