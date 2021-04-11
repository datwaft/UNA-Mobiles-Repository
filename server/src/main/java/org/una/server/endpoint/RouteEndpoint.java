package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.RouteController;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@ServerEndpoint(value = "/route", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class RouteEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    private static final RouteController controller = RouteController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public void sendToMany(JSONObject message, Function<Session, Boolean> condition) throws IOException, EncodeException {
        if (message == null) return;
        for (var session: sessions) {
            if (condition.apply(session)) {
                session.getBasicRemote().sendObject(controller.processQuery(message, session));
            }
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
            sendToMany(switch (response.optString("action")) {
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
        sessions.remove(session);
    }
}
