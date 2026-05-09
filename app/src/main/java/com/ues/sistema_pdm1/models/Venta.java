package com.ues.sistema_pdm1.models;

public class Venta {
    private int id;
    private int idImportador;
    private int idVehiculo;
    private String fechaVenta;
    private double precio;

    public Venta() {}

    public Venta(int id, int idImportador, int idVehiculo, String fechaVenta, double precio) {
        this.id = id;
        this.idImportador = idImportador;
        this.idVehiculo = idVehiculo;
        this.fechaVenta = fechaVenta;
        this.precio = precio;
    }

    public int getId() { return id; }
    public int getIdImportador() { return idImportador; }
    public int getIdVehiculo() { return idVehiculo; }
    public String getFechaVenta() { return fechaVenta; }
    public double getPrecio() { return precio; }

    public void setId(int id) { this.id = id; }
    public void setIdImportador(int idImportador) { this.idImportador = idImportador; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public void setFechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() { return "Venta #" + id; }
}
