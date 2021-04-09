package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.una.server.util.TransformUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalTime;

public class ScheduleDBA {
    private static ScheduleDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray viewAllWithDiscount() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call view_all_schedule_with_discount() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("origin", rs.getString("origin"));
            object.put("destination", rs.getString("destination"));
            object.put("departure_time", rs.getTime("departure_time"));
            object.put("weekday", TransformUtils.transformWeekday(rs.getInt("weekday")));
            object.put("duration", rs.getTime("duration"));
            object.put("price", rs.getDouble("price"));
            object.put("discount", rs.getDouble("discount"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray getAll() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_schedule() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("route", rs.getInt("route"));
            object.put("departure_time", rs.getTime("departure_time"));
            object.put("weekday", TransformUtils.transformWeekday(rs.getInt("weekday")));
            object.put("discount", rs.getDouble("discount"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public void create(Integer route, LocalTime departureTime, String weekday, BigDecimal discount) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_schedule( ?, ?, ?, ? )");
        query.setInt(1, route);
        query.setObject(2, departureTime);
        query.setInt(3, TransformUtils.transformWeekday(weekday));
        query.setBigDecimal(4, discount);
        query.execute();
        query.close();
    }

    public void update(Integer identifier, Integer route, LocalTime departureTime, String weekday, BigDecimal discount) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call update_schedule( ?, ?, ?, ?, ? )");
        query.setInt(1, identifier);
        query.setInt(2, route);
        query.setObject(3, departureTime);
        query.setInt(4, TransformUtils.transformWeekday(weekday));
        query.setBigDecimal(5, discount);
        query.execute();
        query.close();
    }

    public static ScheduleDBA getInstance() {
        if (instance == null) instance = new ScheduleDBA();
        return instance;
    }
}
