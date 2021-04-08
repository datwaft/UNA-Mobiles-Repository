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
    private static final Set<Session> sessions = new HashSet<>();
    private static final TicketController ticketController = TicketController.getInstance();

    public void broadcast(JSONObject message) throws IOException, EncodeException {
        for (var session: sessions) {
            session.getBasicRemote().sendObject(ticketController.processQuery(message));
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(JSONObject message, Session session) throws IOException, EncodeException {
        var response = ticketController.processQuery(message);
        if (response != null) {
            session.getBasicRemote().sendObject(response);
            if (response.optString("type").equals("CREATE")) {
                var _message = new JSONObject();
                _message.put("type", "VIEW_ALL_PER_PURCHASE");
                _message.put("purchase", message.get("purchase"));
                broadcast(_message);
                _message.clear();
                _message.put("type", "VIEW_ALL_PER_FLIGHT");
                _message.put("flight", message.get("flight"));
                broadcast(_message);
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
