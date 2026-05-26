package saberPro.usecases.user;

import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class InhabilitarUsuarioUseCase {

    private final UsuarioRepository repository;

    public InhabilitarUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(int idUsuario) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(idUsuario);

        boolean encontrado = repository.buscar(usuario);

        if (!encontrado) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        if (!usuario.isHabilitado()) {
            throw new IllegalStateException("Este usuario ya está deshabilitado.");
        }

        repository.eliminar(usuario);
    }
}