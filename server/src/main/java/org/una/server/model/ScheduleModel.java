package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.ScheduleDBA;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalTime;

public class ScheduleModel {
    private static ScheduleModel instance = null;

    private final ScheduleDBA dba;

    private ScheduleModel() {
        this.dba = ScheduleDBA.getInstance();
    }

    public static ScheduleModel getInstance() {
        if (instance == null) instance = new ScheduleModel();
        return instance;
    }

    public JSONArray viewAllWithDiscount() throws SQLException {
        return this.dba.viewAllWithDiscount();
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public void create(Integer route, LocalTime departureTime, String weekday, BigDecimal discount) throws SQLException {
        this.dba.create(route, departureTime, weekday, discount);
    }

    public void update(Integer identifier, Integer route, LocalTime departureTime, String weekday, BigDecimal discount) throws SQLException {
        this.dba.update(identifier, route, departureTime, weekday, discount);
    }
}
