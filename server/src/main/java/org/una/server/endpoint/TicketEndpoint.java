package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.TicketController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@ServerEndpoint(value = "/ticket", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class TicketEndpoint {
    private static final TicketController controller = TicketController.getInstance();

    private final Set<Session> sessions = new HashSet<>();

    public void sendToMany(JSONObject message, Predicate<Session> condition) throws EncodeException, IOException {
        if (message == null) return;
        for (var session: sessions) {
            if (condition.test(session)) {
                session.getBasicRemote().sendObject(controller.processQuery(message));
            }
        }
    }

    public void broadcast(JSONObject message) throws EncodeException, IOException {
        if (message == null) return;
        for (var session: sessions) {
            session.getBasicRemote().sendObject(controller.processQuery(message));
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("action").equals("CREATE")) {
                this.broadcast(new JSONObject()
                        .put("action", "VIEW_ALL_PER_PURCHASE")
                        .put("purchase", message.get("purchase"))
                );
                this.broadcast(new JSONObject()
                        .put("action", "VIEW_ALL_PER_FLIGHT")
                        .put("flight", message.get("flight"))
                );
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
