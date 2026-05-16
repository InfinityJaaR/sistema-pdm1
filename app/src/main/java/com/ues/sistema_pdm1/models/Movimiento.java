package com.ues.sistema_pdm1.models;

public class Movimiento {
    private int id;
    private int idTransporte;
    private int idPersonal;
    private int idVehiculo;
    private int idBodega;
    private String tipoMovimiento;
    private String fechaMovimiento;
    private String motivo;

    public Movimiento() {}

    public Movimiento(int id, int idTransporte, int idPersonal, int idVehiculo, int idBodega,
                      String tipoMovimiento, String fechaMovimiento, String motivo) {
        this.id = id;
        this.idTransporte = idTransporte;
        this.idPersonal = idPersonal;
        this.idVehiculo = idVehiculo;
        this.idBodega = idBodega;
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = fechaMovimiento;
        this.motivo = motivo;
    }

    public int getId() { return id; }
    public int getIdTransporte() { return idTransporte; }
    public int getIdPersonal() { return idPersonal; }
    public int getIdVehiculo() { return idVehiculo; }
    public int getIdBodega() { return idBodega; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public String getFechaMovimiento() { return fechaMovimiento; }
    public String getMotivo() { return motivo; }

    public void setId(int id) { this.id = id; }
    public void setIdTransporte(int idTransporte) { this.idTransporte = idTransporte; }
    public void setIdPersonal(int idPersonal) { this.idPersonal = idPersonal; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setIdBodega(int idBodega) { this.idBodega = idBodega; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    @Override
    public String toString() { return tipoMovimiento + " — " + fechaMovimiento; }
}
