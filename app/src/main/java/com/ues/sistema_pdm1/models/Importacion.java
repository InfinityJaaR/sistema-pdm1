package com.ues.sistema_pdm1.models;

public class Importacion {
    private int id;
    private int idImportador;
    private String fechaImportacion;

    public Importacion() {}

    public Importacion(int id, int idImportador, String fechaImportacion) {
        this.id = id;
        this.idImportador = idImportador;
        this.fechaImportacion = fechaImportacion;
    }

    public int getId() { return id; }
    public int getIdImportador() { return idImportador; }
    public String getFechaImportacion() { return fechaImportacion; }

    public void setId(int id) { this.id = id; }
    public void setIdImportador(int idImportador) { this.idImportador = idImportador; }
    public void setFechaImportacion(String fechaImportacion) { this.fechaImportacion = fechaImportacion; }

    @Override
    public String toString() { return "Importación #" + id; }
}
