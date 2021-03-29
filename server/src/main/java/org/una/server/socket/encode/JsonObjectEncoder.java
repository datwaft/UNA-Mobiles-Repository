package org.una.server.socket.encode;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import org.json.JSONObject;

public class JsonObjectEncoder implements Encoder.Text<JSONObject> {

    @Override
    public String encode(JSONObject object) throws EncodeException {
        return object.toString();
    }

    @Override
    public void init(EndpointConfig config) { }

    @Override
    public void destroy() { }
}
