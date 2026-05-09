package com.ues.sistema_pdm1.models;

public class FotoDesperfecto {
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
    public int getIdDetalleDesperfecto() { return idDetalleDesperfecto; }
    public String getRutaImagen() { return rutaImagen; }
    public String getFechaToma() { return fechaToma; }

    public void setId(int id) { this.id = id; }
    public void setIdDetalleDesperfecto(int idDetalleDesperfecto) { this.idDetalleDesperfecto = idDetalleDesperfecto; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    public void setFechaToma(String fechaToma) { this.fechaToma = fechaToma; }

    @Override
    public String toString() { return rutaImagen; }
}
