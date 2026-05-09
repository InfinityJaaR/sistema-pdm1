package com.ues.sistema_pdm1.models;

public class Movimiento {
    private int id;
    private int idVehiculo;
    private int idPersonal;
    private int idTransporte;
    private int idBodegaDestino;
    private String fechaMovimiento;
    private String motivo;
    private String observacion;

    public Movimiento() {}

    public Movimiento(int id, int idVehiculo, int idPersonal, int idTransporte, int idBodegaDestino,
                      String fechaMovimiento, String motivo, String observacion) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idPersonal = idPersonal;
        this.idTransporte = idTransporte;
        this.idBodegaDestino = idBodegaDestino;
        this.fechaMovimiento = fechaMovimiento;
        this.motivo = motivo;
        this.observacion = observacion;
    }

    public int getId() { return id; }
    public int getIdVehiculo() { return idVehiculo; }
    public int getIdPersonal() { return idPersonal; }
    public int getIdTransporte() { return idTransporte; }
    public int getIdBodegaDestino() { return idBodegaDestino; }
    public String getFechaMovimiento() { return fechaMovimiento; }
    public String getMotivo() { return motivo; }
    public String getObservacion() { return observacion; }

    public void setId(int id) { this.id = id; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setIdPersonal(int idPersonal) { this.idPersonal = idPersonal; }
    public void setIdTransporte(int idTransporte) { this.idTransporte = idTransporte; }
    public void setIdBodegaDestino(int idBodegaDestino) { this.idBodegaDestino = idBodegaDestino; }
    public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    @Override
    public String toString() { return "Movimiento #" + id; }
}
