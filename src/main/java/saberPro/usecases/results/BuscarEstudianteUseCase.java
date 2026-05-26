package saberPro.usecases.results;

import saberPro.entities.ResultadoSaberPro;
import saberPro.usecases.ports.ResultadosRepository;

import java.util.List;

public class BuscarEstudianteUseCase {

    private final ResultadosRepository repository;

    public BuscarEstudianteUseCase(ResultadosRepository repository) {
        this.repository = repository;
    }

    public List<ResultadoSaberPro> ejecutar(String nombre, String cc) throws Exception {
        boolean sinNombre = nombre == null || nombre.trim().isEmpty();
        boolean sinCc     = cc     == null || cc.trim().isEmpty();

        if (sinNombre && sinCc) {
            throw new IllegalArgumentException(
                    "Ingrese al menos un criterio de búsqueda (nombre o cédula).");
        }

        List<ResultadoSaberPro> resultado = repository.buscarEstudiantes(nombre, cc);

        if (resultado.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontraron estudiantes con esos criterios.");
        }

        return resultado;
    }
}