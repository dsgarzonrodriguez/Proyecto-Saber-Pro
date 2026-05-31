package com.saberpro.entities;

public class ModuloSaber {

    private String nombre;
    private Double puntaje;
    private Double percentil;

    public ModuloSaber() {}

    public ModuloSaber(String nombre, Double puntaje, Double percentil) {
        this.nombre = nombre;
        this.puntaje = puntaje;
        this.percentil = percentil;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPuntaje() { return puntaje; }
    public void setPuntaje(Double puntaje) { this.puntaje = puntaje; }

    public Double getPercentil() { return percentil; }
    public void setPercentil(Double percentil) { this.percentil = percentil; }
}