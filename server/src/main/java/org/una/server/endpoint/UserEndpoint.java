package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.UserController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

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
        var response = UserController.getInstance().processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.get("action") == "REGISTER") {
                var loginMessage = new JSONObject();
                loginMessage.put("action", "LOGIN");
                loginMessage.put("username", message.get("username"));
                loginMessage.put("password", message.get("password"));
                session.getBasicRemote().sendObject(UserController.getInstance().processQuery(loginMessage, session));
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
        UserController.getInstance().logout(session);
    }
}
