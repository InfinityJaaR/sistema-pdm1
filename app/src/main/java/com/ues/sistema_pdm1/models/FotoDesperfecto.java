package com.ues.sistema_pdm1.models;

import java.io.Serializable;

public class FotoDesperfecto implements Serializable {
    private int id;
    private int idDetalleDesperfecto;
    private String rutaImagen;
    private String fechaToma;

    public FotoDesperfecto() {}

    public FotoDesperfecto(int id, int idDetalleDesperfecto, String rutaImagen, String fechaToma) {
        this.id = id;
        this.idDetalleDesperfecto = idDetalleDesperfecto;
        this.rutaImagen = rutaImagen;
        this.fechaToma = fechaToma;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    public String getFechaToma() { return fechaToma; }
    public void setFechaToma(String fechaToma) { this.fechaToma = fechaToma; }

    @Override
    public String toString() { return rutaImagen; }
}