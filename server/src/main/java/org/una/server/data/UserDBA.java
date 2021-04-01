package org.una.server.data;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
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

    public void updateUser(String username, String name, String lastname, String email, String address,
                           String workphone, String mobilephone) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_user( ?, ?, ?, ?, ?, ?, ? )");
        query.setString(1, username);
        query.setString(2, name);
        query.setString(3, lastname);
        query.setString(4, email);
        query.setString(5, address);
        query.setString(6, workphone);
        query.setString(7, mobilephone);
        query.execute();
        query.close();
    }

    public JSONObject getUser(String username) throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_user( ? ) }");
        query.registerOutParameter(1, Types.OTHER);
        query.setString(2, username);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var object = new JSONObject();
        if (rs.next()) {
            object.put("name", rs.getString("name"));
            object.put("lastname", rs.getString("lastname"));
            object.put("email", rs.getString("email"));
            object.put("address", rs.getString("address"));
            object.put("workphone", rs.getString("workphone"));
            object.put("mobilephone", rs.getString("mobilephone"));
        }
        rs.close();
        query.close();
        return object;
    }

    public static UserDBA getInstance() {
        if (instance == null) instance = new UserDBA();
        return instance;
    }
}
