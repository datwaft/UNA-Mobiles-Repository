package org.una.server.socket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.UserController;
import org.una.server.socket.decode.JsonObjectDecoder;
import org.una.server.socket.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/user", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class UserEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = UserController.getInstance().processQuery(message);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
    }
}
