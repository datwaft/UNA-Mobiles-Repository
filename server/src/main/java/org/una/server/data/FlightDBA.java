package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class FlightDBA {
    private static FlightDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public static FlightDBA getInstance() {
        if (instance == null) instance = new FlightDBA();
        return instance;
    }

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
            object.put("inbound_date", rs.getObject("inbound_date", LocalDate.class));
            object.put("inbound_schedule", rs.getObject("inbound_schedule", Integer.class));
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
        if (inboundDate == null) {
            query.setNull(4, Types.DATE);
        } else {
            query.setObject(4, inboundDate);
        }
        if (inboundSchedule == null) {
            query.setNull(5, Types.INTEGER);
        } else {
            query.setInt(5, inboundSchedule);
        }
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
        if (inboundDate == null) {
            query.setNull(5, Types.DATE);
        } else {
            query.setObject(5, inboundDate);
        }
        if (inboundSchedule == null) {
            query.setNull(6, Types.INTEGER);
        } else {
            query.setInt(6, inboundSchedule);
        }
        query.execute();
        query.close();
    }

    public void delete(Integer identifier) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call delete_flight( ? )");
        query.setInt(1, identifier);
        query.execute();
        query.close();
    }
}
