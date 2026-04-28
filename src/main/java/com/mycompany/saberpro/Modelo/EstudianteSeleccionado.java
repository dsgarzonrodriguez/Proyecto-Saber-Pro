/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Modelo;

/**
 *
 * @author juanf
 */
public class EstudianteSeleccionado {

    private static String cc;
    private static String numeroRegistro;
    private static Integer anio;
    private static Integer semestre;

    public static void setSeleccion(String cc, String numeroRegistro,
            Integer anio, Integer semestre) {
        EstudianteSeleccionado.cc = cc;
        EstudianteSeleccionado.numeroRegistro = numeroRegistro;
        EstudianteSeleccionado.anio = anio;
        EstudianteSeleccionado.semestre = semestre;
    }

    public static String getCc() {
        return cc;
    }

    public static String getNumeroRegistro() {
        return numeroRegistro;
    }

    public static Integer getAnio() {
        return anio;
    }

    public static Integer getSemestre() {
        return semestre;
    }

    public static void limpiar() {
        cc = null;
        numeroRegistro = null;
        anio = null;
        semestre = null;
    }

}
