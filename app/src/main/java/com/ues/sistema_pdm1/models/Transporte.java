package com.ues.sistema_pdm1.models;

public class Transporte {
    private int id;
    private int idTipoTransporte;
    private String placa;
    private String descripcion;
    private int capacidadDiaria;
    private int capacidadActual;

    public Transporte() {}

    public Transporte(int id, int idTipoTransporte, String placa, String descripcion, int capacidadDiaria, int capacidadActual) {
        this.id = id;
        this.idTipoTransporte = idTipoTransporte;
        this.placa = placa;
        this.descripcion = descripcion;
        this.capacidadDiaria = capacidadDiaria;
        this.capacidadActual = capacidadActual;
    }

    public int getId() { return id; }
    public int getIdTipoTransporte() { return idTipoTransporte; }
    public String getPlaca() { return placa; }
    public String getDescripcion() { return descripcion; }
    public int getCapacidadDiaria() { return capacidadDiaria; }
    public int getCapacidadActual() { return capacidadActual; }

    public void setId(int id) { this.id = id; }
    public void setIdTipoTransporte(int idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }
    public void setPlaca(String placa) { this.placa = placa; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCapacidadDiaria(int capacidadDiaria) { this.capacidadDiaria = capacidadDiaria; }
    public void setCapacidadActual(int capacidadActual) { this.capacidadActual = capacidadActual; }

    @Override
    public String toString() { return placa; }
}
