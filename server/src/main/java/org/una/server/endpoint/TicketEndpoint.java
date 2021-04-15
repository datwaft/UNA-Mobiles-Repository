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

@ServerEndpoint(value = "/ticket", decoders = {JsonObjectDecoder.class}, encoders = {JsonObjectEncoder.class})
public class TicketEndpoint {
    private static final TicketController controller = TicketController.getInstance();

    @OnOpen
    public void onOpen(Session session) {
        controller.addSession(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = controller.processQuery(message);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("type").equals("CREATE")) {
                controller.broadcast(new JSONObject()
                        .put("type", "VIEW_ALL_PER_PURCHASE")
                        .put("purchase", message.get("purchase"))
                );
                controller.broadcast(new JSONObject()
                        .put("type", "VIEW_ALL_PER_FLIGHT")
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
        controller.removeSession(session);
    }
}
