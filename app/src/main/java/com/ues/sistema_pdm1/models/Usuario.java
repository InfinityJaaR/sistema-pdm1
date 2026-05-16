package com.ues.sistema_pdm1.models;

public class Usuario {
    private String id;
    private String nomUsuario;
    private String clave;

    public Usuario() {}

    public Usuario(String id, String nomUsuario, String clave) {
        this.id = id;
        this.nomUsuario = nomUsuario;
        this.clave = clave;
    }

    public String getId() { return id; }
    public String getNomUsuario() { return nomUsuario; }
    public String getClave() { return clave; }

    public void setId(String id) { this.id = id; }
    public void setNomUsuario(String nomUsuario) { this.nomUsuario = nomUsuario; }
    public void setClave(String clave) { this.clave = clave; }

    @Override
    public String toString() { return nomUsuario; }
}