package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PlaneTypeDBA {
    private static PlaneTypeDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public static PlaneTypeDBA getInstance() {
        if (instance == null) instance = new PlaneTypeDBA();
        return instance;
    }

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_plane_type() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("year", rs.getInt("year"));
            object.put("model", rs.getString("model"));
            object.put("brand", rs.getString("brand"));
            object.put("rows", rs.getInt("rows"));
            object.put("columns", rs.getInt("columns"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public void create(Integer year, String model, String brand, Integer rows, Integer columns) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_plane_type( ?, ?, ?, ?, ? )");
        query.setInt(1, year);
        query.setString(2, model);
        query.setString(3, brand);
        query.setInt(4, rows);
        query.setInt(5, columns);
        query.execute();
        query.close();
    }

    public void update(Integer identifier, Integer year, String model, String brand, Integer rows, Integer columns) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_plane_type( ?, ?, ?, ?, ?, ? )");
        query.setInt(1, identifier);
        query.setInt(2, year);
        query.setString(3, model);
        query.setString(4, brand);
        query.setInt(5, rows);
        query.setInt(6, columns);
        query.execute();
        query.close();
    }


    public void delete(Integer identifier) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call delete_plane_type( ? )");
        query.setInt(1, identifier);
        query.execute();
        query.close();
    }
}
