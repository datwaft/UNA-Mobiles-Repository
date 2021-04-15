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

    public JSONArray purchaseCountPerMonthInLastYear() throws SQLException {
        return this.dba.purchaseCountPerMonthInLastYear();
    }

    public static ReportModel getInstance() {
        if (instance == null) instance = new ReportModel();
        return instance;
    }
}
