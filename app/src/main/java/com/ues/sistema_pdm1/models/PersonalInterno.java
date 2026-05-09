package com.ues.sistema_pdm1.models;

public class PersonalInterno {
    private int id;
    private String nombrePersonal;
    private String apellidoPersonal;
    private String cargo;

    public PersonalInterno() {}

    public PersonalInterno(int id, String nombrePersonal, String apellidoPersonal, String cargo) {
        this.id = id;
        this.nombrePersonal = nombrePersonal;
        this.apellidoPersonal = apellidoPersonal;
        this.cargo = cargo;
    }

    public int getId() { return id; }
    public String getNombrePersonal() { return nombrePersonal; }
    public String getApellidoPersonal() { return apellidoPersonal; }
    public String getCargo() { return cargo; }

    public void setId(int id) { this.id = id; }
    public void setNombrePersonal(String nombrePersonal) { this.nombrePersonal = nombrePersonal; }
    public void setApellidoPersonal(String apellidoPersonal) { this.apellidoPersonal = apellidoPersonal; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String toString() { return nombrePersonal + " " + apellidoPersonal; }
}
