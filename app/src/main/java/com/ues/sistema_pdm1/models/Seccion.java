package com.ues.sistema_pdm1.models;

public class Seccion {
    private int id;
    private int idBodega;
    private int nivel;
    private int capacidadMaxima;
    private int capacidadActual;

    public Seccion() {}

    public Seccion(int id, int idBodega, int nivel, int capacidadMaxima, int capacidadActual) {
        this.id = id;
        this.idBodega = idBodega;
        this.nivel = nivel;
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadActual = capacidadActual;
    }

    public int getId() { return id; }
    public int getIdBodega() { return idBodega; }
    public int getNivel() { return nivel; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public int getCapacidadActual() { return capacidadActual; }

    public void setId(int id) { this.id = id; }
    public void setIdBodega(int idBodega) { this.idBodega = idBodega; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public void setCapacidadActual(int capacidadActual) { this.capacidadActual = capacidadActual; }

    @Override
    public String toString() { return "Sección " + nivel + " - " + capacidadActual + "/" + capacidadMaxima; }
}
