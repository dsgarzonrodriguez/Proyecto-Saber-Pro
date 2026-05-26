package saberPro.usecases.results;

import saberPro.entities.ResultadoSaberPro;
import saberPro.usecases.ports.ResultadosRepository;

public class ConsultarResultadoPersonalUseCase {

    private final ResultadosRepository repository;

    public ConsultarResultadoPersonalUseCase(ResultadosRepository repository) {
        this.repository = repository;
    }

    public ResultadoSaberPro ejecutar(String cc, String numeroRegistro,
                                      Integer anio, Integer semestre) throws Exception {
        if (cc == null || cc.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula del estudiante es obligatoria.");
        }
        if (numeroRegistro == null || numeroRegistro.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de registro es obligatorio.");
        }
        if (anio == null || semestre == null) {
            throw new IllegalArgumentException("El año y semestre son obligatorios.");
        }

        ResultadoSaberPro resultado = repository.consultarPersonal(cc, numeroRegistro, anio, semestre);

        if (resultado == null) {
            throw new IllegalArgumentException(
                    "No se encontró un resultado para el estudiante con los datos indicados.");
        }

        return resultado;
    }
}