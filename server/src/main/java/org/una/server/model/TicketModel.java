package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.ScheduleDBA;
import org.una.server.data.TicketDBA;

import java.sql.SQLException;

public class TicketModel {
    private static TicketModel instance = null;

    private final TicketDBA dba;

    private TicketModel() {
        this.dba = TicketDBA.getInstance();
    }

    public void create(Integer purchase, Integer row, Integer column) throws SQLException {
        this.dba.create(purchase, row, column);
    }

    public JSONArray viewAllPerFlight(Integer flight) throws SQLException {
        return this.dba.viewAllPerFlight(flight);
    }

    public JSONArray viewAllPerPurchase(Integer purchase) throws SQLException {
        return this.dba.viewAllPerPurchase(purchase);
    }

    public static TicketModel getInstance() {
        if (instance == null) instance = new TicketModel();
        return instance;
    }
}
