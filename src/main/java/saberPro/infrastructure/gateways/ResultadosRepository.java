package saberPro.infrastructure.gateways;

import saberPro.entities.FiltrosConsulta;
import saberPro.entities.ResultadoSaberPro;
import java.util.List;

public interface ResultadosRepository {

    List<ResultadoSaberPro> consultarGenerales(FiltrosConsulta filtros);
    ResultadoSaberPro consultarPersonal(String cc, String numeroRegistro, Integer anio, Integer semestre);
}