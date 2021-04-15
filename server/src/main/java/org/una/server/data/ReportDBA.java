package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.una.server.util.TransformUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ReportDBA {
    private static ReportDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public JSONArray purchaseCountPerMonthInLastYear() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call purchase_count_per_month_in_last_year() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("year", rs.getInt("year"));
            object.put("month", TransformUtils.transformMonth(rs.getInt("month")));
            object.put("count", rs.getInt("count"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray revenuePerMonthInLastYear() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call revenue_per_month_in_last_year() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("year", rs.getInt("year"));
            object.put("month", TransformUtils.transformMonth(rs.getInt("month")));
            object.put("revenue", rs.getInt("revenue"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray clientsPerPlane() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call clients_per_plane() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("name", rs.getString("name"));
            object.put("client", rs.getString("client"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray top5RoutesPerTicketNumber() throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call top_5_routes_per_ticket_number() }");
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("origin", rs.getString("origin"));
            object.put("destination", rs.getString("destination"));
            object.put("ticket_number", rs.getInt("ticket_number"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public static ReportDBA getInstance() {
        if (instance == null) instance = new ReportDBA();
        return instance;
    }
}
