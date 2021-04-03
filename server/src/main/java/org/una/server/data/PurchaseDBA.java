package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PurchaseDBA {
    private static PurchaseDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void create(Integer tickerNumber, Integer flight, String username) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_purchase( ?, ?, ? )");
        query.setInt(1, tickerNumber);
        query.setInt(2, flight);
        query.setString(3, username);
        query.execute();
        query.close();
    }

    public JSONArray viewAll(String username) throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call view_all_purchase( ? ) }");
        query.setString(2, username);
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("user", rs.getString("user"));
            object.put("origin", rs.getString("origin"));
            object.put("destination", rs.getString("destination"));
            object.put("outbound_date", rs.getDate("outbound_date"));
            object.put("inbound_date", rs.getDate("inbound_date"));
            object.put("ticket_amount", rs.getInt("ticket_amount"));
            object.put("total_cost", rs.getDouble("total_cost"));
            object.put("has_been_reserved", rs.getBoolean("has_been_reserved"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public static PurchaseDBA getInstance() {
        if (instance == null) instance = new PurchaseDBA();
        return instance;
    }
}
