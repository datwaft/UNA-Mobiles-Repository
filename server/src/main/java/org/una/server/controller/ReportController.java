package org.una.server.controller;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.ReportModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ReportController {
    private static ReportController instance = null;

    private static final ReportModel model = ReportModel.getInstance();

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
                case "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR" -> purchaseCountPerMonthInLastYear(object.optString("token"), session);
                default -> null;
            };
        } catch (JSONException ex) {
            return null;
        }
    }

    public JSONObject purchaseCountPerMonthInLastYear(String token, Session session) {
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
            var report = model.purchaseCountPerMonthInLastYear();
            response.put("action", "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR");
            response.put("report", report);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public static ReportController getInstance() {
        if (instance == null) instance = new ReportController();
        return instance;
    }
}
