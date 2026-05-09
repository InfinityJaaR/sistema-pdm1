package com.ues.sistema_pdm1.models;

public class TipoVehiculo {
    private int id;
    private String descripcion;

    public TipoVehiculo() {}

    public TipoVehiculo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }

    public void setId(int id) { this.id = id; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return descripcion; }
}
