package com.ues.sistema_pdm1.models;

public class Departamento {
    private int id;
    private int idPais;
    private String nombreDepartamento;

    public Departamento() {}

    public Departamento(int id, int idPais, String nombreDepartamento) {
        this.id = id;
        this.idPais = idPais;
        this.nombreDepartamento = nombreDepartamento;
    }

    public int getId() { return id; }
    public int getIdPais() { return idPais; }
    public String getNombreDepartamento() { return nombreDepartamento; }

    public void setId(int id) { this.id = id; }
    public void setIdPais(int idPais) { this.idPais = idPais; }
    public void setNombreDepartamento(String nombreDepartamento) { this.nombreDepartamento = nombreDepartamento; }

    @Override
    public String toString() { return nombreDepartamento; }
}