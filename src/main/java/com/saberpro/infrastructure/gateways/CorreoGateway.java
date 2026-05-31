package com.saberpro.infrastructure.gateways;

public interface CorreoGateway {

    void enviarCodigo(String correoDestino, String codigo) throws Exception;
}