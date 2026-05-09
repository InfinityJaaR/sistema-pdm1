package com.ues.sistema_pdm1.models;

public class DetalleDesperfecto {
    private int id;
    private int idVehiculo;
    private int idTipoDesperfecto;
    private String descripcionDetalle;
    private String estado;

    public DetalleDesperfecto() {}

    public DetalleDesperfecto(int id, int idVehiculo, int idTipoDesperfecto, String descripcionDetalle, String estado) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idTipoDesperfecto = idTipoDesperfecto;
        this.descripcionDetalle = descripcionDetalle;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getIdVehiculo() { return idVehiculo; }
    public int getIdTipoDesperfecto() { return idTipoDesperfecto; }
    public String getDescripcionDetalle() { return descripcionDetalle; }
    public String getEstado() { return estado; }

    public void setId(int id) { this.id = id; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setIdTipoDesperfecto(int idTipoDesperfecto) { this.idTipoDesperfecto = idTipoDesperfecto; }
    public void setDescripcionDetalle(String descripcionDetalle) { this.descripcionDetalle = descripcionDetalle; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return descripcionDetalle; }
}
