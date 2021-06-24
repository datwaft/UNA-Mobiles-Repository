package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.ReportDBA;

import java.sql.SQLException;

public class ReportModel {
    private static ReportModel instance = null;

    private final ReportDBA dba;

    private ReportModel() {
        this.dba = ReportDBA.getInstance();
    }

    public static ReportModel getInstance() {
        if (instance == null) instance = new ReportModel();
        return instance;
    }

    public JSONArray purchaseCountPerMonthInLastYear() throws SQLException {
        return this.dba.purchaseCountPerMonthInLastYear();
    }

    public JSONArray revenuePerMonthInLastYear() throws SQLException {
        return this.dba.revenuePerMonthInLastYear();
    }

    public JSONArray clientsPerPlane() throws SQLException {
        return this.dba.clientsPerPlane();
    }

    public JSONArray top5RoutesPerTicketNumber() throws SQLException {
        return this.dba.top5RoutesPerTicketNumber();
    }
}
