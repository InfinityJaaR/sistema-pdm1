package com.ues.sistema_pdm1.models;

public class AccesoUsuario {
    private String idOpcion;
    private String idUsuario;

    public AccesoUsuario() {}

    public AccesoUsuario(String idOpcion, String idUsuario) {
        this.idOpcion = idOpcion;
        this.idUsuario = idUsuario;
    }

    public String getIdOpcion() { return idOpcion; }
    public String getIdUsuario() { return idUsuario; }

    public void setIdOpcion(String idOpcion) { this.idOpcion = idOpcion; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public String toString() { return "Opción " + idOpcion + " Usuario " + idUsuario; }
}