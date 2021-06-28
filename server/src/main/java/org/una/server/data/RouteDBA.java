package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalTime;

public class RouteDBA {
    private static RouteDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public static RouteDBA getInstance() {
        if (instance == null) instance = new RouteDBA();
        return instance;
    }

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_route() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("origin", rs.getString("origin"));
            object.put("destination", rs.getString("destination"));
            object.put("duration", rs.getTime("duration"));
            object.put("price", rs.getDouble("price"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public void create(String origin, String destination, LocalTime duration, Integer price) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_route( ?, ?, ?, ? )");
        query.setString(1, origin);
        query.setString(2, destination);
        query.setObject(3, duration);
        query.setInt(4, price);
        query.execute();
        query.close();
    }

    public void update(Integer identifier, String origin, String destination, LocalTime duration, Integer price) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_route( ?, ?, ?, ?, ? )");
        query.setInt(1, identifier);
        query.setString(2, origin);
        query.setString(3, destination);
        query.setObject(4, duration);
        query.setInt(5, price);
        query.execute();
        query.close();
    }

    public void delete(Integer identifier) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call delete_route( ? )");
        query.setInt(1, identifier);
        query.execute();
        query.close();
    }
}
