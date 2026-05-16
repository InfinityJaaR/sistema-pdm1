package com.ues.sistema_pdm1.models;

public class TelefonoImportador {
    private int id;
    private int idImportador;
    private String numero;
    private String tipo;

    public TelefonoImportador() {}

    public TelefonoImportador(int id, int idImportador, String numero, String tipo) {
        this.id = id;
        this.idImportador = idImportador;
        this.numero = numero;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public int getIdImportador() { return idImportador; }
    public String getNumero() { return numero; }
    public String getTipo() { return tipo; }

    public void setId(int id) { this.id = id; }
    public void setIdImportador(int idImportador) { this.idImportador = idImportador; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() { return numero; }
}
