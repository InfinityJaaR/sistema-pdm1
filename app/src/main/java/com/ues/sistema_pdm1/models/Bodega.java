package com.ues.sistema_pdm1.models;

public class Bodega {
    private int id;
    private int idDistrito;
    private String nombreBodega;
    private String direccionBodega;
    private int capacidadTotal;
    private int capacidadActual;

    public Bodega() {}

    public Bodega(int id, int idDistrito, String nombreBodega, String direccionBodega, int capacidadTotal, int capacidadActual) {
        this.id = id;
        this.idDistrito = idDistrito;
        this.nombreBodega = nombreBodega;
        this.direccionBodega = direccionBodega;
        this.capacidadTotal = capacidadTotal;
        this.capacidadActual = capacidadActual;
    }

    public int getId() { return id; }
    public int getIdDistrito() { return idDistrito; }
    public String getNombreBodega() { return nombreBodega; }
    public String getDireccionBodega() { return direccionBodega; }
    public int getCapacidadTotal() { return capacidadTotal; }
    public int getCapacidadActual() { return capacidadActual; }

    public void setId(int id) { this.id = id; }
    public void setIdDistrito(int idDistrito) { this.idDistrito = idDistrito; }
    public void setNombreBodega(String nombreBodega) { this.nombreBodega = nombreBodega; }
    public void setDireccionBodega(String direccionBodega) { this.direccionBodega = direccionBodega; }
    public void setCapacidadTotal(int capacidadTotal) { this.capacidadTotal = capacidadTotal; }
    public void setCapacidadActual(int capacidadActual) { this.capacidadActual = capacidadActual; }

    @Override
    public String toString() { return nombreBodega; }
}
