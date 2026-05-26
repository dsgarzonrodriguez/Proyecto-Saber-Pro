package saberPro.infrastructure.persistence.repositories;

import saberPro.entities.FiltrosConsulta;
import saberPro.infrastructure.config.PostgresConexion;
import saberPro.usecases.ports.InformePort;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformePortImpl implements InformePort {

    private final PostgresConexion conexion = new PostgresConexion();

    @Override
    public EstadisticasGlobales consultarEstadisticasGlobales(FiltrosConsulta filtros)
            throws SQLException {

        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (filtros.getAnio() != null) {
            where.append("AND ano=? ");
            params.add(filtros.getAnio());
        }
        if (filtros.getSemestre() != null) {
            where.append("AND semestre=? ");
            params.add(filtros.getSemestre());
        }
        if (filtros.getPrograma() != null && !filtros.getPrograma().trim().isEmpty()) {
            where.append("AND programa=? ");
            params.add(filtros.getPrograma());
        }
        if (filtros.getCiudad() != null && !filtros.getCiudad().trim().isEmpty()) {
            where.append("AND ciudad=? ");
            params.add(filtros.getCiudad());
        }

        String sql =
            "SELECT COUNT(x) AS n, " +
            "COALESCE(AVG(x),0) AS media, " +
            "COALESCE(VAR_SAMP(x),0) AS varianza, " +
            "COALESCE(STDDEV_SAMP(x),0) AS sd " +
            "FROM (SELECT puntaje_global AS x " +
            "      FROM vista_resultados_detalle " +
            where +
            "      AND puntaje_global IS NOT NULL " +
            "      AND puntaje_global > 0) t";

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long n       = rs.getLong("n");
                double media = rs.getDouble("media");
                double var   = rs.getDouble("varianza");
                double sd    = rs.getDouble("sd");
                double cv    = (media != 0) ? (sd / media) * 100.0 : 0.0;
                return new EstadisticasGlobales(n, media, var, cv);
            }
        }

        return new EstadisticasGlobales(0, 0, 0, 0);
    }

    @Override
    public Map<String, EstadisticasModulo> consultarEstadisticasPorModulo(FiltrosConsulta filtros)
            throws SQLException {

        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (filtros.getAnio() != null) {
            where.append("AND ano=? ");
            params.add(filtros.getAnio());
        }
        if (filtros.getSemestre() != null) {
            where.append("AND semestre=? ");
            params.add(filtros.getSemestre());
        }
        if (filtros.getPrograma() != null && !filtros.getPrograma().trim().isEmpty()) {
            where.append("AND programa=? ");
            params.add(filtros.getPrograma());
        }
        if (filtros.getCiudad() != null && !filtros.getCiudad().trim().isEmpty()) {
            where.append("AND ciudad=? ");
            params.add(filtros.getCiudad());
        }

        String sql =
            "SELECT modulo, " +
            "COUNT(*) AS n, " +
            "COALESCE(AVG(puntaje_modulo),0) AS media, " +
            "COALESCE(VAR_SAMP(puntaje_modulo),0) AS varianza, " +
            "COALESCE(STDDEV_SAMP(puntaje_modulo),0) AS sd " +
            "FROM vista_resultados_modulo_detalle " +
            where +
            "AND puntaje_modulo IS NOT NULL " +
            "AND puntaje_modulo > 0 " +
            "GROUP BY modulo";

        Map<String, EstadisticasModulo> mapa = new HashMap<>();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String modulo  = rs.getString("modulo");
                long n         = rs.getLong("n");
                double media   = rs.getDouble("media");
                double var     = rs.getDouble("varianza");
                double sd      = rs.getDouble("sd");
                double cv      = (media != 0) ? (sd / media) * 100.0 : 0.0;

                if (modulo != null) {
                    mapa.put(
                        modulo.trim().toUpperCase(),
                        new EstadisticasModulo(n, media, var, cv)
                    );
                }
            }
        }

        return mapa;
    }
}