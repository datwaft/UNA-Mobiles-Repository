package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/session", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class SessionEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    private static final SessionController controller = SessionController.getInstance();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s%n", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        controller.logout(session);
    }
}
