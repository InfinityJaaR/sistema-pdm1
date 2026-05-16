package com.ues.sistema_pdm1.models;

public class Reparacion {
    private int id;
    private int idTaller;
    private int idVehiculo;
    private String fechaInicio;
    private String fechaFin;
    private String descripcionTrabajo;
    private int aptoParaVenta;
    private int requiereOtraReparacion;

    public Reparacion() {}

    public Reparacion(int id, int idTaller, int idVehiculo, String fechaInicio, String fechaFin,
                      String descripcionTrabajo, int aptoParaVenta, int requiereOtraReparacion) {
        this.id = id;
        this.idTaller = idTaller;
        this.idVehiculo = idVehiculo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcionTrabajo = descripcionTrabajo;
        this.aptoParaVenta = aptoParaVenta;
        this.requiereOtraReparacion = requiereOtraReparacion;
    }

    public int getId() { return id; }
    public int getIdTaller() { return idTaller; }
    public int getIdVehiculo() { return idVehiculo; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public int getAptoParaVenta() { return aptoParaVenta; }
    public int getRequiereOtraReparacion() { return requiereOtraReparacion; }

    public void setId(int id) { this.id = id; }
    public void setIdTaller(int idTaller) { this.idTaller = idTaller; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }
    public void setAptoParaVenta(int aptoParaVenta) { this.aptoParaVenta = aptoParaVenta; }
    public void setRequiereOtraReparacion(int requiereOtraReparacion) { this.requiereOtraReparacion = requiereOtraReparacion; }

    @Override
    public String toString() { return "Reparación #" + id; }
}
