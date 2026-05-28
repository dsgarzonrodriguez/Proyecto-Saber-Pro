package saberPro.usecases.ports;

import saberPro.entities.FiltrosConsulta;
import saberPro.entities.ResultadoSaberPro;

import java.sql.SQLException;
import java.util.List;

public interface ResultadosRepository {

    List<ResultadoSaberPro> consultarGenerales(FiltrosConsulta filtros) throws Exception;
    ResultadoSaberPro consultarPersonal(String cc, String numeroRegistro, Integer anio, Integer semestre) throws Exception;
    List<ResultadoSaberPro> buscarEstudiantes(String nombre, String cc) throws Exception;
}