package saberPro.usecases.user;

import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;


public class RegistrarUsuarioUseCase {

    private static final String REGEX_CONTRASENA =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$";
    private static final String REGEX_TELEFONO = "\\d{10}";

    private final UsuarioRepository repository;

    public RegistrarUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(Usuario usuario) throws SQLException {
        if (usuario.getContrasena() == null ||
                !usuario.getContrasena().matches(REGEX_CONTRASENA)) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, " +
                    "una letra, un número y un carácter especial (.!@#$%&*-_).");
        }

        if (usuario.getTelefono() == null ||
                !usuario.getTelefono().matches(REGEX_TELEFONO)) {
            throw new IllegalArgumentException(
                    "El teléfono debe tener exactamente 10 dígitos numéricos.");
        }

        // Las validaciones de correo y CC duplicadas las lanza el repositorio
        // como SQLException con mensaje descriptivo (ya implementado en UsuarioRepositoryImpl)
        repository.registrar(usuario);
    }
}