package com.saberpro.usecases.results;
import com.saberpro.usecases.ports.CargaResultadosRepository;
import com.saberpro.usecases.ports.ResultadosRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta la carga masiva de resultados desde un CSV.
 *
 * El tipo de archivo se determina por su nombre exacto:
 *   estudiantes.csv | registro.csv | modulos.csv | ciudad.csv |
 *   programa.csv | resultados.csv | resultados_modulo.csv
 *
 * Cada fila se procesa de forma independiente: si una falla
 * se acumula el error y se continúa con las siguientes.
 */
public class CargaMasivaResultadosUseCase {

    public enum TipoArchivo {
        ESTUDIANTES, REGISTRO, MODULOS, CIUDAD, PROGRAMA, RESULTADOS, RESULTADOS_MODULO;

        public static TipoArchivo desdNombre(String nombreArchivo) {
            return switch (nombreArchivo.toLowerCase().trim()) {
                case "estudiantes.csv"      -> ESTUDIANTES;
                case "registro.csv"         -> REGISTRO;
                case "modulos.csv"          -> MODULOS;
                case "ciudad.csv"           -> CIUDAD;
                case "programa.csv"         -> PROGRAMA;
                case "resultados.csv"       -> RESULTADOS;
                case "resultados_modulo.csv"-> RESULTADOS_MODULO;
                default -> null;
            };
        }
    }

    private final CargaResultadosRepository repository;

    public CargaMasivaResultadosUseCase(CargaResultadosRepository repository) {
        this.repository = repository;
    }

    /**
     * @param tipoArchivo Tipo determinado desde el nombre del archivo.
     * @param filas       Lista de arrays String[], cada uno es una fila del CSV ya parseada.
     * @return {@link ResultadoCarga} con conteo de éxitos y lista de errores.
     */
    public ResultadoCarga ejecutar(TipoArchivo tipoArchivo, List<String[]> filas) {
        int exitosos = 0;
        List<String> errores = new ArrayList<>();

        for (int i = 0; i < filas.size(); i++) {
            int numFila = i + 2; // fila 1 = cabecera
            String[] campos = filas.get(i);

            try {
                validarCamposMinimos(tipoArchivo, campos, numFila);

                switch (tipoArchivo) {
                    case ESTUDIANTES       -> repository.cargarEstudiante(campos);
                    case REGISTRO          -> repository.cargarRegistro(campos);
                    case MODULOS           -> repository.cargarModulo(campos);
                    case CIUDAD            -> repository.cargarCiudad(campos);
                    case PROGRAMA          -> repository.cargarPrograma(campos);
                    case RESULTADOS        -> repository.cargarResultados(campos);
                    case RESULTADOS_MODULO -> repository.cargarResultadosModulo(campos);
                }
                exitosos++;

            } catch (IllegalArgumentException e) {
                errores.add("Fila " + numFila + ": " + e.getMessage());
            } catch (SQLException e) {
                errores.add("Fila " + numFila + ": " + e.getMessage());
            }
        }

        return new ResultadoCarga(exitosos, errores);
    }

    // ── Validación básica de columnas mínimas ────────────────────────────────

    private void validarCamposMinimos(TipoArchivo tipo, String[] campos, int fila) {
        int minimo = switch (tipo) {
            case ESTUDIANTES        -> 5;
            case REGISTRO           -> 5;
            case MODULOS            -> 1;
            case CIUDAD             -> 1;
            case PROGRAMA           -> 2;
            case RESULTADOS         -> 6;
            case RESULTADOS_MODULO  -> 5;
        };

        if (campos.length < minimo) {
            throw new IllegalArgumentException(
                    "Se esperaban al menos " + minimo + " columnas, " +
                    "pero se encontraron " + campos.length + ".");
        }

        for (int i = 0; i < minimo; i++) {
            if (campos[i] == null || campos[i].isBlank()) {
                throw new IllegalArgumentException(
                        "La columna " + (i + 1) + " está vacía.");
            }
        }
    }

    // ── Objeto de resultado ──────────────────────────────────────────────────

    public static class ResultadoCarga {
        private final int          exitosos;
        private final List<String> errores;

        public ResultadoCarga(int exitosos, List<String> errores) {
            this.exitosos = exitosos;
            this.errores  = errores;
        }

        public int          getExitosos() { return exitosos; }
        public List<String> getErrores()  { return errores; }
        public int          getFallidos()  { return errores.size(); }
        public boolean      hayErrores()   { return !errores.isEmpty(); }
    }
}