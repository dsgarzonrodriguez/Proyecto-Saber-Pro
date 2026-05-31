package com.saberpro.infrastructure.persistence.repositories;

import com.saberpro.usecases.ports.CargaResultadosRepository;
import com.saberpro.infrastructure.config.PostgresConexion;

import java.sql.*;

public class CargaResultadosRepositoryImpl implements CargaResultadosRepository {

    private final PostgresConexion conexion = new PostgresConexion();

    @Override
    public void cargarEstudiante(String[] campos) throws SQLException {
        String nombre   = campos[0].trim();
        String apellido = campos[1].trim();
        String telefono = campos[2].trim();
        String cc       = campos[3].trim();
        String correo   = campos[4].trim();

        try (Connection conn = conexion.getConexion()) {

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.id_usuario FROM usuario u " +
                    "JOIN estudiante e ON e.id_usuario = u.id_usuario " +
                    "WHERE u.correo = ? AND e.cc = ?")) {
                ps.setString(1, correo);
                ps.setString(2, cc);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return;
                }
            }

            String contrasena = generarContrasena();
            int idRolEstudiante = 4;

            int idUsuario;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuario (correo, contrasena, id_roles, habilitado) " +
                    "VALUES (?, ?, ?, TRUE) RETURNING id_usuario")) {
                ps.setString(1, correo);
                ps.setString(2, contrasena);
                ps.setInt(3, idRolEstudiante);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("No se pudo crear el usuario.");
                    idUsuario = rs.getInt("id_usuario");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO historial (id_usuario, id_roles) VALUES (?, ?)")) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idRolEstudiante);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO estudiante (nombre, apellido, telefono, cc, id_usuario) " +
                    "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, nombre);
                ps.setString(2, apellido);
                ps.setString(3, telefono);
                ps.setString(4, cc);
                ps.setInt(5, idUsuario);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarRegistro(String[] campos) throws SQLException {
        String numeroRegistro = campos[0].trim();
        String tipoEvaluado   = campos[1].trim();
        String cc             = campos[2].trim();
        int    ano            = Integer.parseInt(campos[3].trim());
        int    semestre       = Integer.parseInt(campos[4].trim());

        try (Connection conn = conexion.getConexion()) {

            int idEstudiante;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_estudiante FROM estudiante WHERE cc = ?")) {
                ps.setString(1, cc);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("No existe estudiante con CC: " + cc);
                    idEstudiante = rs.getInt("id_estudiante");
                }
            }

            int idRegistro;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO registro (codigo, tipo_evaluado) VALUES (?, ?) " +
                    "RETURNING id_registro")) {
                ps.setString(1, numeroRegistro);
                ps.setString(2, tipoEvaluado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("No se pudo insertar el registro.");
                    idRegistro = rs.getInt("id_registro");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO prueba (id_estudiante, id_registro, ano, semestre) " +
                    "VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idRegistro);
                ps.setInt(3, ano);
                ps.setInt(4, semestre);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarModulo(String[] campos) throws SQLException {
        String nombre = campos[0].trim();
        try (Connection conn = conexion.getConexion();
             PreparedStatement check = conn.prepareStatement(
                     "SELECT id_modulos FROM modulos WHERE nombre = ?")) {
            check.setString(1, nombre);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO modulos (nombre) VALUES (?)")) {
                ps.setString(1, nombre);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarCiudad(String[] campos) throws SQLException {
        String nombre = campos[0].trim();
        try (Connection conn = conexion.getConexion();
             PreparedStatement check = conn.prepareStatement(
                     "SELECT id_ciudad FROM ciudad WHERE nombre = ?")) {
            check.setString(1, nombre);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ciudad (nombre) VALUES (?)")) {
                ps.setString(1, nombre);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarPrograma(String[] campos) throws SQLException {
        String nombre = campos[0].trim();
        int    snies  = Integer.parseInt(campos[1].trim());
        try (Connection conn = conexion.getConexion();
             PreparedStatement check = conn.prepareStatement(
                     "SELECT id_programa FROM programa WHERE snies = ?")) {
            check.setInt(1, snies);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO programa (nombre, snies) VALUES (?, ?)")) {
                ps.setString(1, nombre);
                ps.setInt(2, snies);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarResultados(String[] campos) throws SQLException {
        String moduloUnico     = campos[0].trim();
        String numeroRegistro  = campos[1].trim();
        double puntajeGlobal   = Double.parseDouble(campos[2].trim());
        double percentilGlobal = Double.parseDouble(campos[3].trim());
        String nombreCiudad    = campos[4].trim();
        String nombrePrograma  = campos[5].trim();

        try (Connection conn = conexion.getConexion()) {
            int idCiudad   = buscarId(conn, "ciudad",   "nombre", nombreCiudad,   "id_ciudad");
            int idPrograma = buscarId(conn, "programa", "nombre", nombrePrograma, "id_programa");
            int idRegistro = buscarId(conn, "registro", "codigo", numeroRegistro, "id_registro");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resultados " +
                    "(puntaje_global, porcentaje_nacional_global, id_ciudad, id_programa, id_registro, modulo_unico) " +
                    "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setDouble(1, puntajeGlobal);
                ps.setDouble(2, percentilGlobal);
                ps.setInt(3, idCiudad);
                ps.setInt(4, idPrograma);
                ps.setInt(5, idRegistro);
                ps.setString(6, moduloUnico);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void cargarResultadosModulo(String[] campos) throws SQLException {
        String moduloUnico  = campos[0].trim();
        double puntaje      = Double.parseDouble(campos[1].trim());
        String nivel        = campos[2].trim();
        double percentil    = Double.parseDouble(campos[3].trim());
        String nombreModulo = campos[4].trim();

        try (Connection conn = conexion.getConexion()) {
            int idModulo = buscarId(conn, "modulos", "nombre", nombreModulo, "id_modulos");

            int idResultados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_resultados FROM resultados WHERE modulo_unico = ?")) {
                ps.setString(1, moduloUnico);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException(
                                "No existe resultados con modulo_unico = " + moduloUnico);
                    idResultados = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resultados_modulo " +
                    "(puntaje_modulo, nivel_desempeno, percentil_nacional_modulo, id_modulos, id_resultados) " +
                    "VALUES (?, ?, ?, ?, ?)")) {
                ps.setDouble(1, puntaje);
                ps.setString(2, nivel);
                ps.setDouble(3, percentil);
                ps.setInt(4, idModulo);
                ps.setInt(5, idResultados);
                ps.executeUpdate();
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private int buscarId(Connection conn, String tabla, String campo,
                         String valor, String campoId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT " + campoId + " FROM " + tabla + " WHERE " + campo + " = ?")) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    throw new SQLException(
                            "No se encontró '" + valor + "' en la tabla " + tabla + ".");
                return rs.getInt(1);
            }
        }
    }

    private String generarContrasena() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        return sb.toString();
    }
}