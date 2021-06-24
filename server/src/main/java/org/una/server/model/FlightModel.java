package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.FlightDBA;

import java.sql.SQLException;
import java.time.LocalDate;

public class FlightModel {
    private static FlightModel instance = null;

    private final FlightDBA dba;

    private FlightModel() {
        this.dba = FlightDBA.getInstance();
    }

    public static FlightModel getInstance() {
        if (instance == null) instance = new FlightModel();
        return instance;
    }

    public JSONArray viewAll() throws SQLException {
        return this.dba.viewAll();
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public void create(Integer plane, LocalDate outboundDate, Integer outboundSchedule, LocalDate inboundDate, Integer inboundSchedule) throws SQLException {
        this.dba.create(plane, outboundDate, outboundSchedule, inboundDate, inboundSchedule);
    }

    public void update(Integer identifier, Integer plane, LocalDate outboundDate, Integer outboundSchedule, LocalDate inboundDate, Integer inboundSchedule) throws SQLException {
        this.dba.update(identifier, plane, outboundDate, outboundSchedule, inboundDate, inboundSchedule);
    }
}
