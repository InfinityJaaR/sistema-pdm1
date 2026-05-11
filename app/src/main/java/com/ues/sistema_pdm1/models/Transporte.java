package com.ues.sistema_pdm1.models;

public class Transporte {
    private int id;
    private int idTipoTransporte;
    private String placa;
    private String descripcionTransporte;

    public Transporte() {}

    public Transporte(int id, int idTipoTransporte, String placa, String descripcionTransporte) {
        this.id = id;
        this.idTipoTransporte = idTipoTransporte;
        this.placa = placa;
        this.descripcionTransporte = descripcionTransporte;
    }

    public int getId() { return id; }
    public int getIdTipoTransporte() { return idTipoTransporte; }
    public String getPlaca() { return placa; }
    public String getDescripcionTransporte() { return descripcionTransporte; }

    public void setId(int id) { this.id = id; }
    public void setIdTipoTransporte(int idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }
    public void setPlaca(String placa) { this.placa = placa; }
    public void setDescripcionTransporte(String descripcionTransporte) { this.descripcionTransporte = descripcionTransporte; }

    @Override
    public String toString() { return placa; }
}
