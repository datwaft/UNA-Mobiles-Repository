package org.una.server.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TicketDBA {
    private static TicketDBA instance = null;

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public static TicketDBA getInstance() {
        if (instance == null) instance = new TicketDBA();
        return instance;
    }

    public void create(Integer purchase, Integer row, Integer column) throws SQLException {
        connection.setAutoCommit(true);
        var query = connection.prepareStatement("call create_ticket( ?, ?, ? )");
        query.setInt(1, purchase);
        query.setInt(2, row);
        query.setInt(3, column);
        query.execute();
        query.close();
    }

    public JSONArray viewAllPerFlight(Integer flight) throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call view_all_ticket_per_flight( ? ) }");
        query.setInt(2, flight);
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("row", rs.getInt("row"));
            object.put("column", rs.getInt("column"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }

    public JSONArray viewAllPerPurchase(Integer purchase) throws SQLException {
        connection.setAutoCommit(false);
        var query = connection.prepareCall("{ ? = call view_all_ticket_per_purchase( ? ) }");
        query.setInt(2, purchase);
        query.registerOutParameter(1, Types.OTHER);
        query.execute();
        var rs = (ResultSet) query.getObject(1);
        var result = new JSONArray();
        while (rs.next()) {
            var object = new JSONObject();
            object.put("identifier", rs.getInt("identifier"));
            object.put("row", rs.getInt("row"));
            object.put("column", rs.getInt("column"));
            result.put(object);
        }
        rs.close();
        query.close();
        return result;
    }
}
