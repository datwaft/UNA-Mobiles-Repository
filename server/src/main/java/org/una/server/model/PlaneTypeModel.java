package org.una.server.model;

import org.json.JSONArray;
import org.una.server.data.PlaneTypeDBA;

import java.sql.SQLException;

public class PlaneTypeModel {
    private static PlaneTypeModel instance = null;

    private final PlaneTypeDBA dba;

    private PlaneTypeModel() {
        this.dba = PlaneTypeDBA.getInstance();
    }

    public static PlaneTypeModel getInstance() {
        if (instance == null) instance = new PlaneTypeModel();
        return instance;
    }

    public JSONArray getAll() throws SQLException {
        return this.dba.getAll();
    }

    public void create(Integer year, String model, String brand, Integer rows, Integer columns) throws SQLException {
        this.dba.create(year, model, brand, rows, columns);
    }

    public void update(Integer identifier, Integer year, String model, String brand, Integer rows, Integer columns) throws SQLException {
        this.dba.update(identifier, year, model, brand, rows, columns);
    }

    public void delete(Integer identifier) throws SQLException {
        this.dba.delete(identifier);
    }
}
