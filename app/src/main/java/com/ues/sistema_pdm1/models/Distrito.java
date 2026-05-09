package com.ues.sistema_pdm1.models;

public class Distrito {
    private int id;
    private int idMunicipio;
    private String nombreDistrito;

    public Distrito() {}

    public Distrito(int id, int idMunicipio, String nombreDistrito) {
        this.id = id;
        this.idMunicipio = idMunicipio;
        this.nombreDistrito = nombreDistrito;
    }

    public int getId() { return id; }
    public int getIdMunicipio() { return idMunicipio; }
    public String getNombreDistrito() { return nombreDistrito; }

    public void setId(int id) { this.id = id; }
    public void setIdMunicipio(int idMunicipio) { this.idMunicipio = idMunicipio; }
    public void setNombreDistrito(String nombreDistrito) { this.nombreDistrito = nombreDistrito; }

    @Override
    public String toString() { return nombreDistrito; }
}