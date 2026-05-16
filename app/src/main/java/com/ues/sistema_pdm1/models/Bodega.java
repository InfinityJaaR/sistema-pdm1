package com.ues.sistema_pdm1.models;

public class Bodega {
    private int id;
    private int idDistrito;
    private String nombreBodega;
    private String direccionBodega;

    public Bodega() {}

    public Bodega(int id, int idDistrito, String nombreBodega, String direccionBodega) {
        this.id = id;
        this.idDistrito = idDistrito;
        this.nombreBodega = nombreBodega;
        this.direccionBodega = direccionBodega;
    }

    public int getId() { return id; }
    public int getIdDistrito() { return idDistrito; }
    public String getNombreBodega() { return nombreBodega; }
    public String getDireccionBodega() { return direccionBodega; }

    public void setId(int id) { this.id = id; }
    public void setIdDistrito(int idDistrito) { this.idDistrito = idDistrito; }
    public void setNombreBodega(String nombreBodega) { this.nombreBodega = nombreBodega; }
    public void setDireccionBodega(String direccionBodega) { this.direccionBodega = direccionBodega; }

    @Override
    public String toString() { return nombreBodega; }
}
