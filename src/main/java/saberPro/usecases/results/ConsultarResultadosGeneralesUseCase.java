package saberPro.usecases.results;

import saberPro.entities.FiltrosConsulta;
import saberPro.entities.ResultadoSaberPro;
import saberPro.usecases.ports.ResultadosRepository;

import java.util.List;

public class ConsultarResultadosGeneralesUseCase {

    private final ResultadosRepository repository;

    public ConsultarResultadosGeneralesUseCase(ResultadosRepository repository) {
        this.repository = repository;
    }

    public List<ResultadoSaberPro> ejecutar(FiltrosConsulta filtros) throws Exception {
        if (filtros == null) {
            throw new IllegalArgumentException("Los filtros de consulta son obligatorios.");
        }

        List<ResultadoSaberPro> resultados = repository.consultarGenerales(filtros);

        if (resultados.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontraron resultados con los filtros seleccionados.");
        }

        return resultados;
    }
}