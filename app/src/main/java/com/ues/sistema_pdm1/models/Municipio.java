package com.ues.sistema_pdm1.models;

public class Municipio {
    private int id;
    private int idDepartamento;
    private String nombreMunicipio;

    public Municipio() {}

    public Municipio(int id, int idDepartamento, String nombreMunicipio) {
        this.id = id;
        this.idDepartamento = idDepartamento;
        this.nombreMunicipio = nombreMunicipio;
    }

    public int getId() { return id; }
    public int getIdDepartamento() { return idDepartamento; }
    public String getNombreMunicipio() { return nombreMunicipio; }

    public void setId(int id) { this.id = id; }
    public void setIdDepartamento(int idDepartamento) { this.idDepartamento = idDepartamento; }
    public void setNombreMunicipio(String nombreMunicipio) { this.nombreMunicipio = nombreMunicipio; }

    @Override
    public String toString() { return nombreMunicipio; }
}