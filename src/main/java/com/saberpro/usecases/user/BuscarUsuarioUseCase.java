package com.saberpro.usecases.user;

import com.saberpro.entities.Usuario;
import com.saberpro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class BuscarUsuarioUseCase {

    private final UsuarioRepository repository;

    public BuscarUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario ejecutar(Usuario usuario) throws SQLException {
        boolean tieneId     = usuario.getId_usuario() > 0;
        boolean tieneCorreo = usuario.getCorreo() != null && !usuario.getCorreo().isEmpty();

        if (!tieneId && !tieneCorreo) {
            throw new IllegalArgumentException(
                    "Debe ingresar un ID o un correo para buscar.");
        }

        boolean encontrado = repository.buscar(usuario);

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No se encontró ningún usuario con los datos proporcionados.");
        }

        return usuario;
    }
}