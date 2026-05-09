package com.ues.sistema_pdm1.models;

public class TipoDesperfecto {
    private int id;
    private String nombreTipoDesperfecto;
    private String descripcion;

    public TipoDesperfecto() {}

    public TipoDesperfecto(int id, String nombreTipoDesperfecto, String descripcion) {
        this.id = id;
        this.nombreTipoDesperfecto = nombreTipoDesperfecto;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getNombreTipoDesperfecto() { return nombreTipoDesperfecto; }
    public String getDescripcion() { return descripcion; }

    public void setId(int id) { this.id = id; }
    public void setNombreTipoDesperfecto(String nombreTipoDesperfecto) { this.nombreTipoDesperfecto = nombreTipoDesperfecto; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return nombreTipoDesperfecto; }
}
