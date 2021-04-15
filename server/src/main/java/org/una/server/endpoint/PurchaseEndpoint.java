package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.PurchaseController;
import org.una.server.controller.ReportController;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;

@ServerEndpoint(value = "/purchase", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class PurchaseEndpoint {
    private static final PurchaseController controller = PurchaseController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    private static final ReportController reportController = ReportController.getInstance();

    @OnOpen
    public void onOpen(Session session) {
        controller.addSession(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("action").equals("CREATE")) {
                controller.broadcast(new JSONObject().put("action", "VIEW_ALL"));
                reportController.sendToMany(new JSONObject().put("action", "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR"), sessionController::isSessionAdmin);
                reportController.sendToMany(new JSONObject().put("action", "REVENUE_PER_MONTH_IN_LAST_YEAR"), sessionController::isSessionAdmin);
                reportController.sendToMany(new JSONObject().put("action", "CLIENTS_PER_PLANE"), sessionController::isSessionAdmin);
                reportController.sendToMany(new JSONObject().put("action", "TOP_5_ROUTES_PER_TICKET_NUMBER"), sessionController::isSessionAdmin);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s%n", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        controller.removeSession(session);
    }
}
