package com.saberpro.usecases.user;

import com.saberpro.infrastructure.gateways.CorreoGateway;
import com.saberpro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;
import java.util.Random;

public class EnviarCodigoRecuperacionUseCase {

    private static final int LONGITUD_CODIGO = 6;
    private static final long COOLDOWN_MS = 60_000L;

    private final UsuarioRepository repository;
    private final CorreoGateway correoGateway;

    private long ultimoEnvio = 0;

    public EnviarCodigoRecuperacionUseCase(UsuarioRepository repository, CorreoGateway correoGateway) {
        this.repository = repository;
        this.correoGateway = correoGateway;
    }

    public String ejecutar(String correo) throws Exception {
        if (System.currentTimeMillis() - ultimoEnvio < COOLDOWN_MS) {
            throw new IllegalStateException(
                    "Debe esperar 1 minuto antes de enviar otro código.");
        }

        boolean correoValido = repository.verificarCorreoExiste(correo);
        if (!correoValido) {
            throw new IllegalArgumentException(
                    "El correo no está registrado en el sistema o la cuenta está deshabilitada.");
        }

        String codigo = generarCodigo();
        correoGateway.enviarCodigo(correo, codigo);

        ultimoEnvio = System.currentTimeMillis();
        return codigo;
    }

    private String generarCodigo() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}