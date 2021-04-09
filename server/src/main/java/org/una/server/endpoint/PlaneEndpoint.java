package org.una.server.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.una.server.controller.PlaneController;
import org.una.server.controller.SessionController;
import org.una.server.endpoint.decode.JsonObjectDecoder;
import org.una.server.endpoint.encode.JsonObjectEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@ServerEndpoint(value = "/plane", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class PlaneEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    private static final PlaneController controller = PlaneController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    public void sendToMany(JSONObject message, Function<Session, Boolean> condition) throws IOException, EncodeException {
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
            if (response.optString("action").equals("CREATE")) {
                var _message = new JSONObject();
                _message.put("action", "GET_ALL");
                sendToMany(_message, sessionController::isSessionAdmin);
            }
            if (response.optString("action").equals("UPDATE")) {
                var _message = new JSONObject();
                _message.put("action", "GET_ALL");
                sendToMany(_message, sessionController::isSessionAdmin);
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
