package com.saberpro.usecases.user;

import com.saberpro.entities.Usuario;
import com.saberpro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class LoginUsuarioUseCase {

    private final UsuarioRepository repository;

    public LoginUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario ejecutar(Usuario usuario) throws SQLException {
        boolean encontrado = repository.login(usuario);

        if (!encontrado) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        if (!usuario.isHabilitado()) {
            throw new IllegalStateException("La cuenta está deshabilitada.");
        }

        return usuario;
    }
}