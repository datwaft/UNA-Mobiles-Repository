package org.una.server.controller;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.una.server.model.UserModel;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserController {
    private static UserController instance = null;

    public JSONObject processQuery(JSONObject object) {
        return switch (object.getString("action")) {
            case "LOGIN" -> login(object.getString("username"), object.getString("password"));
            default -> null;
        };
    }

    public JSONObject login(String username, String password) {
        try {
            var response = new JSONObject();
            response.put("action", "LOGIN");
            response.put("value", UserModel.getInstance().canLogIn(username, password));
            return response;
        } catch (SQLException ex) {
            System.err.format("SQLException: %s%n", ex.getMessage());
            return null;
        }
    }

    public static UserController getInstance() {
        if (instance == null) instance = new UserController();
        return instance;
    }
}
