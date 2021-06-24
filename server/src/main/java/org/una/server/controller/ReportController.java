package org.una.server.controller;

import jakarta.websocket.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.model.ReportModel;

import java.sql.SQLException;

public class ReportController {
    private static final ReportModel model = ReportModel.getInstance();
    private static final SessionController sessionController = SessionController.getInstance();
    private static ReportController instance = null;

    public static ReportController getInstance() {
        if (instance == null) instance = new ReportController();
        return instance;
    }

    public JSONObject processQuery(JSONObject object, Session session) {
        if (object == null) return null;
        try {
            return switch (object.getString("action")) {
                case "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR" -> purchaseCountPerMonthInLastYear(object.optString("token"), session);
                case "REVENUE_PER_MONTH_IN_LAST_YEAR" -> revenuePerMonthInLastYear(object.optString("token"), session);
                case "CLIENTS_PER_PLANE" -> clientsPerPlane(object.optString("token"), session);
                case "TOP_5_ROUTES_PER_TICKET_NUMBER" -> top5PerTicketNumber(object.optString("token"), session);
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

    public JSONObject revenuePerMonthInLastYear(String token, Session session) {
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
            var report = model.revenuePerMonthInLastYear();
            response.put("action", "REVENUE_PER_MONTH_IN_LAST_YEAR");
            response.put("report", report);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject clientsPerPlane(String token, Session session) {
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
            var report = model.clientsPerPlane();
            response.put("action", "CLIENTS_PER_PLANE");
            response.put("report", report);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }

    public JSONObject top5PerTicketNumber(String token, Session session) {
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
            var report = model.top5RoutesPerTicketNumber();
            response.put("action", "TOP_5_ROUTES_PER_TICKET_NUMBER");
            response.put("report", report);
        } catch (SQLException ex) {
            response.put("action", "ERROR");
            response.put("message", ex.getMessage());
        } catch (JSONException ex) {
            return null;
        }
        return response;
    }
}
