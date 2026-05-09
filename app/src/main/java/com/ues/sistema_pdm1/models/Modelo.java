package com.ues.sistema_pdm1.models;

public class Modelo {
    private int id;
    private int idMarca;
    private String nombreModelo;

    public Modelo() {}

    public Modelo(int id, int idMarca, String nombreModelo) {
        this.id = id;
        this.idMarca = idMarca;
        this.nombreModelo = nombreModelo;
    }

    public int getId() { return id; }
    public int getIdMarca() { return idMarca; }
    public String getNombreModelo() { return nombreModelo; }

    public void setId(int id) { this.id = id; }
    public void setIdMarca(int idMarca) { this.idMarca = idMarca; }
    public void setNombreModelo(String nombreModelo) { this.nombreModelo = nombreModelo; }

    @Override
    public String toString() { return nombreModelo; }
}