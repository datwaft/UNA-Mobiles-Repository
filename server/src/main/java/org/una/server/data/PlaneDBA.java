package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PlaneDBA {
    private static PlaneDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_plane() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("name", rs.getString("name"));
            object.put("type", rs.getInt("type"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public void create(String name, Integer type) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_plane( ?, ? )");
        query.setString(1, name);
        query.setInt(2, type);
        query.execute();
        query.close();
    }

    public void update(Integer identifier, String name, Integer type) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_plane( ?, ?, ? )");
        query.setInt(1, identifier);
        query.setString(2, name);
        query.setInt(3, type);
        query.execute();
        query.close();
    }

    public static PlaneDBA getInstance() {
        if (instance == null) instance = new PlaneDBA();
        return instance;
    }
}
