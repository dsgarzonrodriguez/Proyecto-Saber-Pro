package saberPro.infrastructure.gateways;

import saberPro.entities.ResultadoSaberPro;
import java.util.List;

public interface ExportacionGateway {

    void exportarPDF(List<ResultadoSaberPro> resultados, String rutaDestino) throws Exception;
    void exportarExcel(List<ResultadoSaberPro> resultados, String rutaDestino) throws Exception;
}