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

    public JSONArray getAllWithDiscount() throws SQLException {
        return this.dba.getAllWithDiscount();
    }

    public static ScheduleModel getInstance() {
        if (instance == null) instance = new ScheduleModel();
        return instance;
    }
}
