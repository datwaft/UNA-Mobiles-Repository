package org.una.server.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UserDBA {
    private static UserDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public String getAuthorization(String username, String password) throws SQLException {
        var query = connection.prepareCall("{ ? = call get_authorization( ?, ? ) }");
        query.registerOutParameter(1, Types.VARCHAR);
        query.setString(2, username);
        query.setString(3, password);
        query.execute();
        return query.getString(1);
    }

    public void registerUser(String username, String password, String name, String lastname, String email,
                             String address, String workphone, String mobilephone) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call register_user( ?, ?, ?, ?, ?, ?, ?, ? )");
        query.setString(1, username);
        query.setString(2, password);
        query.setString(3, name);
        query.setString(4, lastname);
        query.setString(5, email);
        query.setString(6, address);
        query.setString(7, workphone);
        query.setString(8, mobilephone);
        query.execute();
        query.close();
    }

    public static UserDBA getInstance() {
        if (instance == null) instance = new UserDBA();
        return instance;
    }
}
