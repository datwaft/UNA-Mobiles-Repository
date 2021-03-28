package org.una.server;

import org.una.server.data.UserDBA;
import org.una.server.websockets.EchoWebSocket;
import spark.Spark;

public class Main {

    public static void main(String[] args) {
    //    Spark.webSocket("/echo", EchoWebSocket.class);
    //    Spark.init();
        try {
            Boolean result = UserDBA.getInstance().canLogIn("user1", "password");
            System.out.println("Can log in: " + (result ? "Yes" : "No"));
        } catch (Exception ex) {
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
}
