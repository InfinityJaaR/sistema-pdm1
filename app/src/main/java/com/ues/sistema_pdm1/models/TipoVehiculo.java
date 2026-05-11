package com.ues.sistema_pdm1.models;

public class TipoVehiculo {
    private int id;
    private String descripcionTipoVehiculo;

    public TipoVehiculo() {}

    public TipoVehiculo(int id, String descripcionTipoVehiculo) {
        this.id = id;
        this.descripcionTipoVehiculo = descripcionTipoVehiculo;
    }

    public int getId() { return id; }
    public String getDescripcionTipoVehiculo() { return descripcionTipoVehiculo; }

    public void setId(int id) { this.id = id; }
    public void setDescripcionTipoVehiculo(String descripcionTipoVehiculo) { this.descripcionTipoVehiculo = descripcionTipoVehiculo; }

    @Override
    public String toString() { return descripcionTipoVehiculo; }
}
