package com.ues.sistema_pdm1.models;

public class Taller {
    private int id;
    private String nombreTaller;
    private String direccionTaller;
    private String telefonoTaller;
    private int autorizado;

    public Taller() {}

    public Taller(int id, String nombreTaller, String direccionTaller, String telefonoTaller, int autorizado) {
        this.id = id;
        this.nombreTaller = nombreTaller;
        this.direccionTaller = direccionTaller;
        this.telefonoTaller = telefonoTaller;
        this.autorizado = autorizado;
    }

    public int getId() { return id; }
    public String getNombreTaller() { return nombreTaller; }
    public String getDireccionTaller() { return direccionTaller; }
    public String getTelefonoTaller() { return telefonoTaller; }
    public int getAutorizado() { return autorizado; }

    public void setId(int id) { this.id = id; }
    public void setNombreTaller(String nombreTaller) { this.nombreTaller = nombreTaller; }
    public void setDireccionTaller(String direccionTaller) { this.direccionTaller = direccionTaller; }
    public void setTelefonoTaller(String telefonoTaller) { this.telefonoTaller = telefonoTaller; }
    public void setAutorizado(int autorizado) { this.autorizado = autorizado; }

    @Override
    public String toString() { return nombreTaller; }
}
