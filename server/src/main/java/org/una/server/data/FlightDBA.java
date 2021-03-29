package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class FlightDBA {
    private static FlightDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_flights() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("origin", rs.getString("origin"));
            object.put("destination", rs.getString("destination"));
            object.put("outbound_date", rs.getDate("outbound_date"));
            object.put("inbound_date", rs.getDate("inbound_date"));
            object.put("passenger_amount", rs.getInt("passenger_amount"));
            object.put("passenger_total", rs.getInt("passenger_total"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public static FlightDBA getInstance() {
        if (instance == null) instance = new FlightDBA();
        return instance;
    }
}
