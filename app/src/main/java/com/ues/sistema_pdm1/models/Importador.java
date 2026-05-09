package com.ues.sistema_pdm1.models;

public class Importador {
    private int id;
    private int idDistrito;
    private String nui;
    private String nombreImportador;
    private String apellidoImportador;
    private String direccionImportador;
    private String emailImportador;
    private String estado;
    private int capacidadBodegaTotal;
    private int capacidadBodegaActual;

    public Importador() {}

    public Importador(int id, int idDistrito, String nui, String nombreImportador, String apellidoImportador,
                      String direccionImportador, String emailImportador, String estado,
                      int capacidadBodegaTotal, int capacidadBodegaActual) {
        this.id = id;
        this.idDistrito = idDistrito;
        this.nui = nui;
        this.nombreImportador = nombreImportador;
        this.apellidoImportador = apellidoImportador;
        this.direccionImportador = direccionImportador;
        this.emailImportador = emailImportador;
        this.estado = estado;
        this.capacidadBodegaTotal = capacidadBodegaTotal;
        this.capacidadBodegaActual = capacidadBodegaActual;
    }

    public int getId() { return id; }
    public int getIdDistrito() { return idDistrito; }
    public String getNui() { return nui; }
    public String getNombreImportador() { return nombreImportador; }
    public String getApellidoImportador() { return apellidoImportador; }
    public String getDireccionImportador() { return direccionImportador; }
    public String getEmailImportador() { return emailImportador; }
    public String getEstado() { return estado; }
    public int getCapacidadBodegaTotal() { return capacidadBodegaTotal; }
    public int getCapacidadBodegaActual() { return capacidadBodegaActual; }

    public void setId(int id) { this.id = id; }
    public void setIdDistrito(int idDistrito) { this.idDistrito = idDistrito; }
    public void setNui(String nui) { this.nui = nui; }
    public void setNombreImportador(String nombreImportador) { this.nombreImportador = nombreImportador; }
    public void setApellidoImportador(String apellidoImportador) { this.apellidoImportador = apellidoImportador; }
    public void setDireccionImportador(String direccionImportador) { this.direccionImportador = direccionImportador; }
    public void setEmailImportador(String emailImportador) { this.emailImportador = emailImportador; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCapacidadBodegaTotal(int capacidadBodegaTotal) { this.capacidadBodegaTotal = capacidadBodegaTotal; }
    public void setCapacidadBodegaActual(int capacidadBodegaActual) { this.capacidadBodegaActual = capacidadBodegaActual; }

    @Override
    public String toString() { return nombreImportador + " " + apellidoImportador; }
}
