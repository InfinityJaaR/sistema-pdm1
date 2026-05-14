package com.ues.sistema_pdm1.models;

public class TipoTransporte {
    private int id;
    private String descripcionTipoTransporte;
    private int capacidadMaxVehiculos;

    public TipoTransporte() {}

    public TipoTransporte(int id, String descripcionTipoTransporte, int capacidadMaxVehiculos) {
        this.id = id;
        this.descripcionTipoTransporte = descripcionTipoTransporte;
        this.capacidadMaxVehiculos = capacidadMaxVehiculos;
    }

    public int getId() { return id; }
    public String getDescripcionTipoTransporte() { return descripcionTipoTransporte; }
    public int getCapacidadMaxVehiculos() { return capacidadMaxVehiculos; }

    public void setId(int id) { this.id = id; }
    public void setDescripcionTipoTransporte(String descripcionTipoTransporte) { this.descripcionTipoTransporte = descripcionTipoTransporte; }
    public void setCapacidadMaxVehiculos(int capacidadMaxVehiculos) { this.capacidadMaxVehiculos = capacidadMaxVehiculos; }

    @Override
    public String toString() { return descripcionTipoTransporte; }
}
