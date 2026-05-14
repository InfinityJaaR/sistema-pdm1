package com.ues.sistema_pdm1.models;

import java.io.Serializable;

public class DetalleDesperfecto implements Serializable {
    private int id;
    private int idVehiculo;
    private int idTipoDesperfecto;
    private String descripcionDetalle;
    private String fechaRegistro;

    public DetalleDesperfecto() {}

    public DetalleDesperfecto(int id, int idVehiculo, int idTipoDesperfecto, String descripcionDetalle, String fechaRegistro) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idTipoDesperfecto = idTipoDesperfecto;
        this.descripcionDetalle = descripcionDetalle;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public int getIdTipoDesperfecto() { return idTipoDesperfecto; }
    public void setIdTipoDesperfecto(int idTipoDesperfecto) { this.idTipoDesperfecto = idTipoDesperfecto; }
    public String getDescripcionDetalle() { return descripcionDetalle; }
    public void setDescripcionDetalle(String descripcionDetalle) { this.descripcionDetalle = descripcionDetalle; }
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() { return descripcionDetalle; }
}