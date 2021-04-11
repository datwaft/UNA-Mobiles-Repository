package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;

public class FlightDBA {
    private static FlightDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray viewAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call view_all_flight() }");
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
            object.put("ticket_price", rs.getDouble("ticket_price"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_flight() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("plane", rs.getInt("plane"));
            object.put("outbound_date", rs.getDate("outbound_date"));
            object.put("outbound_schedule", rs.getInt("outbound_schedule"));
            object.put("inbound_date", rs.getDate("inbound_date"));
            object.put("inbound_schedule", rs.getInt("inbound_schedule"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public void create(Integer plane, LocalDate outboundDate, Integer outboundSchedule, LocalDate inboundDate, Integer inboundSchedule) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_flight( ?, ?, ?, ?, ? )");
        query.setInt(1, plane);
        query.setObject(2, outboundDate);
        query.setInt(3, outboundSchedule);
        query.setObject(4, inboundDate);
        query.setInt(5, inboundSchedule);
        query.execute();
        query.close();
    }

    public void update(Integer identifier, Integer plane, LocalDate outboundDate, Integer outboundSchedule, LocalDate inboundDate, Integer inboundSchedule) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_flight( ?, ?, ?, ?, ?, ? )");
        query.setInt(1, identifier);
        query.setInt(2, plane);
        query.setObject(3, outboundDate);
        query.setInt(4, outboundSchedule);
        query.setObject(5, inboundDate);
        query.setInt(6, inboundSchedule);
        query.execute();
        query.close();
    }

    public static FlightDBA getInstance() {
        if (instance == null) instance = new FlightDBA();
        return instance;
    }
}
