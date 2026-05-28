package saberPro.infrastructure.gateways;

import saberPro.entities.FiltrosConsulta;

public interface InformeGateway {

    String generarInformeIA(String contexto) throws Exception;
     void guardarInformePDF(String textoInforme, String rutaDestino) throws Exception;
}