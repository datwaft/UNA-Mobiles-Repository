package org.una.server.model;

import org.una.server.data.UserDBA;

import java.sql.SQLException;

public class UserModel {
    private static UserModel instance = null;

    private final UserDBA dba;

    private UserModel() {
        this.dba = UserDBA.getInstance();
    }

    public String getAuthorization(String username, String password) throws SQLException {
        return dba.getAuthorization(username, password);
    }

    public void registerUser(String username, String password, String name, String lastname, String email,
                             String address, String workphone, String mobilephone) throws SQLException {
        dba.registerUser(username, password, name, lastname, email, address, workphone, mobilephone);
    }

    public static UserModel getInstance() {
        if (instance == null) instance = new UserModel();
        return instance;
    }
}
