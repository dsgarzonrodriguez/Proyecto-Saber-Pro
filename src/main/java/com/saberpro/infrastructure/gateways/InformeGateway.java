package com.saberpro.infrastructure.gateways;

import com.saberpro.entities.FiltrosConsulta;

public interface InformeGateway {

    String generarInformeIA(String contexto) throws Exception;
     void guardarInformePDF(String textoInforme, String rutaDestino) throws Exception;
}