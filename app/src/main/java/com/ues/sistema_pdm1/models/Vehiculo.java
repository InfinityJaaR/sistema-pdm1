package com.ues.sistema_pdm1.models;

public class Vehiculo {
    private int id;
    private int idImportacion;
    private int idModelo;
    private int idTipoVehiculo;
    private int idSeccion;
    private String vin;
    private int anio;
    private String estadoVehiculo;
    private String fechaIngreso;

    public Vehiculo() {}

    public Vehiculo(int id, int idImportacion, int idModelo, int idTipoVehiculo, int idSeccion,
                    String vin, int anio, String estadoVehiculo, String fechaIngreso) {
        this.id = id;
        this.idImportacion = idImportacion;
        this.idModelo = idModelo;
        this.idTipoVehiculo = idTipoVehiculo;
        this.idSeccion = idSeccion;
        this.vin = vin;
        this.anio = anio;
        this.estadoVehiculo = estadoVehiculo;
        this.fechaIngreso = fechaIngreso;
    }

    public int getId() { return id; }
    public int getIdImportacion() { return idImportacion; }
    public int getIdModelo() { return idModelo; }
    public int getIdTipoVehiculo() { return idTipoVehiculo; }
    public int getIdSeccion() { return idSeccion; }
    public String getVin() { return vin; }
    public int getAnio() { return anio; }
    public String getEstadoVehiculo() { return estadoVehiculo; }
    public String getFechaIngreso() { return fechaIngreso; }

    public void setId(int id) { this.id = id; }
    public void setIdImportacion(int idImportacion) { this.idImportacion = idImportacion; }
    public void setIdModelo(int idModelo) { this.idModelo = idModelo; }
    public void setIdTipoVehiculo(int idTipoVehiculo) { this.idTipoVehiculo = idTipoVehiculo; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public void setVin(String vin) { this.vin = vin; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setEstadoVehiculo(String estadoVehiculo) { this.estadoVehiculo = estadoVehiculo; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    @Override
    public String toString() { return vin; }
}
