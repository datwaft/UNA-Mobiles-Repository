package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.PurchaseController;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/purchase", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class PurchaseEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    private static final PurchaseController controller = PurchaseController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public void broadcast(JSONObject message) throws IOException, EncodeException {
        if (message == null) return;
        for (var session: sessions) {
            session.getBasicRemote().sendObject(controller.processQuery(message, session));
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("action").equals("CREATE")) {
                broadcast(new JSONObject().put("action", "VIEW_ALL"));
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s%n", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
