package com.saberpro.infrastructure.persistence.repositories;

import com.saberpro.usecases.ports.ResultadosRepository;
import com.saberpro.entities.FiltrosConsulta;
import com.saberpro.entities.ModuloSaber;
import com.saberpro.entities.ResultadoSaberPro;
import com.saberpro.infrastructure.config.PostgresConexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadosRepositoryImpl implements ResultadosRepository {

    private final PostgresConexion conexion = new PostgresConexion();

    @Override
    public List<ResultadoSaberPro> consultarGenerales(FiltrosConsulta filtros) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT ano, semestre, nombre, apellido, cc, numero_registro, " +
            "programa, ciudad, puntaje_global, percentil_global " +
            "FROM vista_resultados_detalle WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filtros.getAnio() != null) {
            sql.append("AND ano=? ");
            params.add(filtros.getAnio());
        }
        if (filtros.getSemestre() != null) {
            sql.append("AND semestre=? ");
            params.add(filtros.getSemestre());
        }
        if (filtros.getPrograma() != null && !filtros.getPrograma().trim().isEmpty()) {
            sql.append("AND programa=? ");
            params.add(filtros.getPrograma());
        }
        if (filtros.getCiudad() != null && !filtros.getCiudad().trim().isEmpty()) {
            sql.append("AND ciudad=? ");
            params.add(filtros.getCiudad());
        }

        sql.append("ORDER BY apellido, nombre");

        List<ResultadoSaberPro> lista = new ArrayList<>();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ResultadoSaberPro r = new ResultadoSaberPro();
                r.setNumeroRegistro(rs.getString("numero_registro"));
                r.setNombre(rs.getString("nombre"));
                r.setApellido(rs.getString("apellido"));
                r.setCc(rs.getString("cc"));
                r.setAnio(rs.getInt("ano"));
                r.setSemestre(rs.getInt("semestre"));
                r.setPrograma(rs.getString("programa"));
                r.setCiudad(rs.getString("ciudad"));
                r.setPuntajeGlobal(rs.getObject("puntaje_global") != null
                    ? rs.getDouble("puntaje_global") : null);
                r.setPercentilGlobal(rs.getObject("percentil_global") != null
                    ? rs.getDouble("percentil_global") : null);
                lista.add(r);
            }
        }
        return lista;
    }

    @Override
    public ResultadoSaberPro consultarPersonal(String cc, String numeroRegistro,
                                               Integer anio, Integer semestre)
            throws SQLException {

        String sqlGlobal =
            "SELECT ano, semestre, nombre, apellido, cc, numero_registro, " +
            "programa, ciudad, puntaje_global, percentil_global " +
            "FROM vista_resultados_detalle " +
            "WHERE cc=? AND numero_registro=? AND ano=? AND semestre=?";

        ResultadoSaberPro resultado = null;
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlGlobal)) {

            ps.setString(1, cc);
            ps.setString(2, numeroRegistro);
            ps.setInt(3, anio);
            ps.setInt(4, semestre);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                resultado = new ResultadoSaberPro();
                resultado.setNumeroRegistro(rs.getString("numero_registro"));
                resultado.setNombre(rs.getString("nombre"));
                resultado.setApellido(rs.getString("apellido"));
                resultado.setCc(rs.getString("cc"));
                resultado.setAnio(rs.getInt("ano"));
                resultado.setSemestre(rs.getInt("semestre"));
                resultado.setPrograma(rs.getString("programa"));
                resultado.setCiudad(rs.getString("ciudad"));
                resultado.setPuntajeGlobal(rs.getObject("puntaje_global") != null
                    ? rs.getDouble("puntaje_global") : null);
                resultado.setPercentilGlobal(rs.getObject("percentil_global") != null
                    ? rs.getDouble("percentil_global") : null);
            }
        }

        if (resultado == null) return null;

        String sqlModulos =
            "SELECT modulo, puntaje_modulo, percentil_nacional_modulo " +
            "FROM vista_resultados_modulo_detalle " +
            "WHERE documento=? AND numero_registro=? AND ano=? AND semestre=?";

        List<ModuloSaber> modulos = new ArrayList<>();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlModulos)) {

            ps.setString(1, cc);
            ps.setString(2, numeroRegistro);
            ps.setInt(3, anio);
            ps.setInt(4, semestre);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modulos.add(new ModuloSaber(
                    rs.getString("modulo"),
                    rs.getDouble("puntaje_modulo"),
                    rs.getDouble("percentil_nacional_modulo")
                ));
            }
        }

        resultado.setModulos(modulos);
        return resultado;
    }

    @Override
    public List<ResultadoSaberPro> buscarEstudiantes(String nombre, String cc)
            throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT nombre, apellido, cc, numero_registro, ano, semestre " +
            "FROM vista_estudiantes_pruebas WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append("AND (LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ?) ");
            String patron = "%" + nombre.trim().toLowerCase() + "%";
            params.add(patron);
            params.add(patron);
        }

        if (cc != null && !cc.trim().isEmpty()) {
            sql.append("AND cc LIKE ? ");
            params.add(cc.trim() + "%");
        }

        sql.append("ORDER BY apellido, nombre, ano DESC, semestre DESC");

        List<ResultadoSaberPro> lista = new ArrayList<>();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ResultadoSaberPro r = new ResultadoSaberPro();
                r.setNombre(rs.getString("nombre"));
                r.setApellido(rs.getString("apellido"));
                r.setCc(rs.getString("cc"));
                r.setNumeroRegistro(rs.getString("numero_registro"));
                r.setAnio(rs.getInt("ano"));
                r.setSemestre(rs.getInt("semestre"));
                lista.add(r);
            }
        }
        return lista;
    }
}