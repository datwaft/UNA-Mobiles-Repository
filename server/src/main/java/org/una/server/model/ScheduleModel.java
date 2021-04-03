package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.ScheduleDBA;

import java.sql.SQLException;

public class ScheduleModel {
    private static ScheduleModel instance = null;

    private final ScheduleDBA dba;

    private ScheduleModel() {
        this.dba = ScheduleDBA.getInstance();
    }

    public JSONArray viewAllWithDiscount() throws SQLException {
        return this.dba.viewAllWithDiscount();
    }

    public static ScheduleModel getInstance() {
        if (instance == null) instance = new ScheduleModel();
        return instance;
    }
}
