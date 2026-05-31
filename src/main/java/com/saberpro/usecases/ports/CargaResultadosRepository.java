package com.saberpro.usecases.ports;

import java.sql.SQLException;

public interface CargaResultadosRepository {

    /** campos: nombre, apellido, telefono, cc, correo */
    void cargarEstudiante(String[] campos) throws SQLException;

    /** campos: numero_registro, tipo_evaluado, cc_estudiante, año, semestre */
    void cargarRegistro(String[] campos) throws SQLException;

    /** campos: nombre */
    void cargarModulo(String[] campos) throws SQLException;

    /** campos: nombre */
    void cargarCiudad(String[] campos) throws SQLException;

    /** campos: nombre, snies */
    void cargarPrograma(String[] campos) throws SQLException;

    /** campos: modulo_unico, numero_registro, puntaje_global, percentil_global, ciudad, programa */
    void cargarResultados(String[] campos) throws SQLException;

    /** campos: modulo_unico, puntaje, nivel_desempeno, percentil, nombre_modulo */
    void cargarResultadosModulo(String[] campos) throws SQLException;
}