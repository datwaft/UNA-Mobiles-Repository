package org.una.server.model;

import org.una.server.data.UserDBA;

import java.sql.SQLException;

public class UserModel {
    private static UserModel instance = null;

    private final UserDBA dba;

    private UserModel() {
        this.dba = UserDBA.getInstance();
    }

    public Boolean canLogIn(String username, String password) throws SQLException {
        return dba.canLogIn(username, password);
    }

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }
}
