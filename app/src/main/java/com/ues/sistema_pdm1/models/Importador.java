package com.ues.sistema_pdm1.models;

public class Importador {
    private int id;
    private int idDistrito;
    private String nombreImportador;
    private String apellidoImportador;
    private String apellidoCasada;
    private String genero;
    private String direccionImportador;
    private String fechaNacimiento;
    private String correoElectronico;
    private String nui;
    private String nombreResponsable;

    public Importador() {}

    public Importador(int id, int idDistrito, String nombreImportador, String apellidoImportador,
                      String apellidoCasada, String genero, String direccionImportador,
                      String fechaNacimiento, String correoElectronico, String nui,
                      String nombreResponsable) {
        this.id = id;
        this.idDistrito = idDistrito;
        this.nombreImportador = nombreImportador;
        this.apellidoImportador = apellidoImportador;
        this.apellidoCasada = apellidoCasada;
        this.genero = genero;
        this.direccionImportador = direccionImportador;
        this.fechaNacimiento = fechaNacimiento;
        this.correoElectronico = correoElectronico;
        this.nui = nui;
        this.nombreResponsable = nombreResponsable;
    }

    public int getId() { return id; }
    public int getIdDistrito() { return idDistrito; }
    public String getNombreImportador() { return nombreImportador; }
    public String getApellidoImportador() { return apellidoImportador; }
    public String getApellidoCasada() { return apellidoCasada; }
    public String getGenero() { return genero; }
    public String getDireccionImportador() { return direccionImportador; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getNui() { return nui; }
    public String getNombreResponsable() { return nombreResponsable; }

    public void setId(int id) { this.id = id; }
    public void setIdDistrito(int idDistrito) { this.idDistrito = idDistrito; }
    public void setNombreImportador(String nombreImportador) { this.nombreImportador = nombreImportador; }
    public void setApellidoImportador(String apellidoImportador) { this.apellidoImportador = apellidoImportador; }
    public void setApellidoCasada(String apellidoCasada) { this.apellidoCasada = apellidoCasada; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setDireccionImportador(String direccionImportador) { this.direccionImportador = direccionImportador; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setNui(String nui) { this.nui = nui; }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }

    @Override
    public String toString() { return nombreImportador + " " + apellidoImportador; }
}
