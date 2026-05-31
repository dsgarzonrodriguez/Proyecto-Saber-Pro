package com.saberpro.usecases.ports;

import com.saberpro.entities.FiltrosConsulta;

import java.sql.SQLException;

public interface InformePort {

    InformePort.EstadisticasGlobales consultarEstadisticasGlobales(FiltrosConsulta filtros) throws SQLException;

    java.util.Map<String, InformePort.EstadisticasModulo> consultarEstadisticasPorModulo(FiltrosConsulta filtros) throws SQLException;

    class EstadisticasGlobales {
        public final long n;
        public final double media;
        public final double varianza;
        public final double cv;

        public EstadisticasGlobales(long n, double media, double varianza, double cv) {
            this.n = n;
            this.media = media;
            this.varianza = varianza;
            this.cv = cv;
        }
    }

    class EstadisticasModulo {
        public final long n;
        public final double media;
        public final double varianza;
        public final double cv;

        public EstadisticasModulo(long n, double media, double varianza, double cv) {
            this.n = n;
            this.media = media;
            this.varianza = varianza;
            this.cv = cv;
        }
    }
}