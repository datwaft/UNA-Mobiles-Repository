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
    private static final UserController controller = UserController.getInstance();

    private static final SessionController sessionController = SessionController.getInstance();

    @OnOpen
    public void onOpen(Session session) {
        controller.addSession(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException, JSONException {
        var response = controller.processQuery(message, session);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("action").equals("UPDATE")) {
                controller.sendToMany(new JSONObject().put("action", "GET"), (s) -> sessionController.shareUsername(s, session));
            }
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
