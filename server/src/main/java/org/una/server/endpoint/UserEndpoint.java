package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONException;
import org.json.JSONObject;
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

    public void sendToMany(JSONObject message, Function<Session, Boolean> condition) throws IOException, EncodeException {
        for (var session: sessions) {
            if (condition.apply(session)) {
                session.getBasicRemote().sendObject(UserController.getInstance().processQuery(message, session));
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException, JSONException {
        var response = UserController.getInstance().processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.get("action") == "REGISTER") {
                var newMessage = new JSONObject();
                newMessage.put("action", "LOGIN");
                newMessage.put("username", message.get("username"));
                newMessage.put("password", message.get("password"));
                session.getBasicRemote().sendObject(UserController.getInstance().processQuery(newMessage, session));
            } else if (response.get("action") == "UPDATE") {
                var newMessage = new JSONObject();
                newMessage.put("action", "GET");
                sendToMany(newMessage, (s) -> UserController.getInstance().accountIsEqual(s, session));
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
        UserController.getInstance().logout(session);
    }
}
