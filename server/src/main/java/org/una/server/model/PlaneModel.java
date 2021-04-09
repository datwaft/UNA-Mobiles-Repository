package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.PlaneDBA;

import java.sql.SQLException;

public class PlaneModel {
    private static PlaneModel instance = null;

    private final PlaneDBA dba;

    private PlaneModel() {
        this.dba = PlaneDBA.getInstance();
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public void create(String name, Integer type) throws SQLException {
        this.dba.create(name, type);
    }

    public void update(Integer identifier, String name, Integer type) throws SQLException {
        this.dba.update(identifier, name, type);
    }

    public static PlaneModel getInstance() {
        if (instance == null) instance = new PlaneModel();
        return instance;
    }
}
