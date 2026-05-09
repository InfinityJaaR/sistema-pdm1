package com.ues.sistema_pdm1.models;

public class OpcionCrud {
    private String id;
    private String desOpcion;
    private int numCrud;

    public OpcionCrud() {}

    public OpcionCrud(String id, String desOpcion, int numCrud) {
        this.id = id;
        this.desOpcion = desOpcion;
        this.numCrud = numCrud;
    }

    public String getId() { return id; }
    public String getDesOpcion() { return desOpcion; }
    public int getNumCrud() { return numCrud; }

    public void setId(String id) { this.id = id; }
    public void setDesOpcion(String desOpcion) { this.desOpcion = desOpcion; }
    public void setNumCrud(int numCrud) { this.numCrud = numCrud; }

    @Override
    public String toString() { return desOpcion; }
}