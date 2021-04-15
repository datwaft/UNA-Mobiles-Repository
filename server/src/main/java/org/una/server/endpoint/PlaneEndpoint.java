package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.PlaneController;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;

@ServerEndpoint(value = "/plane", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class PlaneEndpoint {
    private static final PlaneController controller = PlaneController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    @OnOpen
    public void onOpen(Session session) {
        controller.addSession(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            controller.sendToMany(switch (response.optString("action")) {
                case "CREATE", "UPDATE" -> new JSONObject().put("action", "GET_ALL");
                default -> null;
            }, sessionController::isSessionAdmin);
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
