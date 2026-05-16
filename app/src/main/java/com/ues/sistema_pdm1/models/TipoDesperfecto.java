package com.ues.sistema_pdm1.models;

public class TipoDesperfecto {
    private int id;
    private String nombreTipoDesperfecto;
    private String descripcionTipoDesperfecto;

    public TipoDesperfecto() {}

    public TipoDesperfecto(int id, String nombreTipoDesperfecto, String descripcionTipoDesperfecto) {
        this.id = id;
        this.nombreTipoDesperfecto = nombreTipoDesperfecto;
        this.descripcionTipoDesperfecto = descripcionTipoDesperfecto;
    }

    public int getId() { return id; }
    public String getNombreTipoDesperfecto() { return nombreTipoDesperfecto; }
    public String getDescripcionTipoDesperfecto() { return descripcionTipoDesperfecto; }

    public void setId(int id) { this.id = id; }
    public void setNombreTipoDesperfecto(String nombreTipoDesperfecto) { this.nombreTipoDesperfecto = nombreTipoDesperfecto; }
    public void setDescripcionTipoDesperfecto(String descripcionTipoDesperfecto) { this.descripcionTipoDesperfecto = descripcionTipoDesperfecto; }

    @Override
    public String toString() { return nombreTipoDesperfecto; }
}
