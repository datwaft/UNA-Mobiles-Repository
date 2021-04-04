package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONException;
import org.json.JSONObject;
import org.una.server.controller.SessionController;
import org.una.server.controller.UserController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@ServerEndpoint(value = "/user", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class UserEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    private static final UserController userController = UserController.getInstance();
    private static final SessionController sessionController = SessionController.getInstance();

    public void sendToMany(JSONObject message, Function<Session, Boolean> condition) throws IOException, EncodeException {
        for (var session: sessions) {
            if (condition.apply(session)) {
                session.getBasicRemote().sendObject(userController.processQuery(message, session));
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException, JSONException {
        var response = userController.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("action").equals("UPDATE")) {
                var getMessage = new JSONObject();
                getMessage.put("action", "GET");
                sendToMany(getMessage, (s) -> sessionController.shareUsername(s, session));
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.format("Error for session %s: %s", session.getId(), throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
