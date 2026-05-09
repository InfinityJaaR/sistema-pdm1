package com.ues.sistema_pdm1.models;

public class Marca {
    private int id;
    private String nombreMarca;

    public Marca() {}

    public Marca(int id, String nombreMarca) {
        this.id = id;
        this.nombreMarca = nombreMarca;
    }

    public int getId() { return id; }
    public String getNombreMarca() { return nombreMarca; }

    public void setId(int id) { this.id = id; }
    public void setNombreMarca(String nombreMarca) { this.nombreMarca = nombreMarca; }

    @Override
    public String toString() { return nombreMarca; }
}