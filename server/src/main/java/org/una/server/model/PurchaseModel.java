package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.PurchaseDBA;

import java.sql.SQLException;

public class PurchaseModel {
    private static PurchaseModel instance = null;

    private final PurchaseDBA dba;

    private PurchaseModel() {
        this.dba = PurchaseDBA.getInstance();
    }

    public static PurchaseModel getInstance() {
        if (instance == null) instance = new PurchaseModel();
        return instance;
    }

    public void create(Integer tickerNumber, Integer flight, String username) throws SQLException {
        this.dba.create(tickerNumber, flight, username);
    }

    public JSONArray viewAll(String username) throws SQLException {
        return this.dba.viewAll(username);
    }
}
