package com.saberpro.entities;

import java.util.List;

public class ResultadoSaberPro {

    private String numeroRegistro;
    private String nombre;
    private String apellido;
    private String cc;
    private int anio;
    private int semestre;
    private String programa;
    private String ciudad;
    private Double puntajeGlobal;
    private Double percentilGlobal;
    private List<ModuloSaber> modulos;

    public ResultadoSaberPro() {}

    public ResultadoSaberPro(String numeroRegistro, String nombre, String apellido,
                              String cc, int anio, int semestre,
                              String programa, String ciudad,
                              Double puntajeGlobal, Double percentilGlobal,
                              List<ModuloSaber> modulos) {
        this.numeroRegistro   = numeroRegistro;
        this.nombre           = nombre;
        this.apellido         = apellido;
        this.cc               = cc;
        this.anio             = anio;
        this.semestre         = semestre;
        this.programa         = programa;
        this.ciudad           = ciudad;
        this.puntajeGlobal    = puntajeGlobal;
        this.percentilGlobal  = percentilGlobal;
        this.modulos          = modulos;
    }

    public String getNumeroRegistro() { return numeroRegistro; }
    public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getPuntajeGlobal() { return puntajeGlobal; }
    public void setPuntajeGlobal(Double puntajeGlobal) { this.puntajeGlobal = puntajeGlobal; }

    public Double getPercentilGlobal() { return percentilGlobal; }
    public void setPercentilGlobal(Double percentilGlobal) { this.percentilGlobal = percentilGlobal; }

    public List<ModuloSaber> getModulos() { return modulos; }
    public void setModulos(List<ModuloSaber> modulos) { this.modulos = modulos; }
}