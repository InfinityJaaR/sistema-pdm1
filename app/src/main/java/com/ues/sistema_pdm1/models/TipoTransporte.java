package com.ues.sistema_pdm1.models;

public class TipoTransporte {
    private int id;
    private String descripcion;
    private int capacidadMaxVehiculos;

    public TipoTransporte() {}

    public TipoTransporte(int id, String descripcion, int capacidadMaxVehiculos) {
        this.id = id;
        this.descripcion = descripcion;
        this.capacidadMaxVehiculos = capacidadMaxVehiculos;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public int getCapacidadMaxVehiculos() { return capacidadMaxVehiculos; }

    public void setId(int id) { this.id = id; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCapacidadMaxVehiculos(int capacidadMaxVehiculos) { this.capacidadMaxVehiculos = capacidadMaxVehiculos; }

    @Override
    public String toString() { return descripcion; }
}