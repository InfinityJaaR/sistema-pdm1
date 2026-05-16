package com.ues.sistema_pdm1.models;

public class Pais {
    private int id;
    private String nombrePais;

    public Pais() {}

    public Pais(int id, String nombrePais) {
        this.id = id;
        this.nombrePais = nombrePais;
    }

    public int getId() { return id; }
    public String getNombrePais() { return nombrePais; }

    public void setId(int id) { this.id = id; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }

    @Override
    public String toString() { return nombrePais; }
}