package com.ues.sistema_pdm1.models;

public class Reparacion {
    private int id;
    private int idVehiculo;
    private int idTaller;
    private String fechaInicio;
    private String fechaFin;
    private int aptoParaVenta;
    private double costo;
    private String observacion;

    public Reparacion() {}

    public Reparacion(int id, int idVehiculo, int idTaller, String fechaInicio, String fechaFin,
                      int aptoParaVenta, double costo, String observacion) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idTaller = idTaller;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aptoParaVenta = aptoParaVenta;
        this.costo = costo;
        this.observacion = observacion;
    }

    public int getId() { return id; }
    public int getIdVehiculo() { return idVehiculo; }
    public int getIdTaller() { return idTaller; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public int getAptoParaVenta() { return aptoParaVenta; }
    public double getCosto() { return costo; }
    public String getObservacion() { return observacion; }

    public void setId(int id) { this.id = id; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setIdTaller(int idTaller) { this.idTaller = idTaller; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public void setAptoParaVenta(int aptoParaVenta) { this.aptoParaVenta = aptoParaVenta; }
    public void setCosto(double costo) { this.costo = costo; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    @Override
    public String toString() { return "Reparación #" + id; }
}
