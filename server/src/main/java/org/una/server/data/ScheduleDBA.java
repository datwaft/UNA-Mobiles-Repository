package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.una.server.util.Transform;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ScheduleDBA {
    private static ScheduleDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray getAllWithDiscount() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call get_all_schedules_with_discount() }");
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
            object.put("weekday", Transform.transformWeekday(rs.getInt("weekday")));
            object.put("duration", rs.getTime("duration"));
            object.put("price", rs.getDouble("price"));
            object.put("discount", rs.getDouble("discount"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public static ScheduleDBA getInstance() {
        if (instance == null) instance = new ScheduleDBA();
        return instance;
    }
}
