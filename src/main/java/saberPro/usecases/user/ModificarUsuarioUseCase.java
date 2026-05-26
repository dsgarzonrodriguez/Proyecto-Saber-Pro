package saberPro.usecases.user;

import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class ModificarUsuarioUseCase {

    private static final String REGEX_CONTRASENA =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$";
    private static final String REGEX_TELEFONO = "\\d{10}";

    private final UsuarioRepository repository;

    public ModificarUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(Usuario usuario, int idOriginal) throws SQLException {
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

        repository.modificar(usuario, idOriginal);
    }
}