package saberPro.infrastructure.gateways;

import saberPro.entities.FiltrosConsulta;

public interface InformeGateway {

    String generarInformeIA(String contexto) throws Exception;
}