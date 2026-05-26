package saberPro.usecases.user;

import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class ActualizarCuentaUseCase {

    private static final String REGEX_CONTRASENA =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$";
    private static final String REGEX_TELEFONO = "\\d{10}";

    private final UsuarioRepository repository;

    public ActualizarCuentaUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(Usuario usuarioActualizado, String contrasenaActual) throws SQLException {
        String nuevaContrasena = usuarioActualizado.getContrasena();

        if (nuevaContrasena == null || !nuevaContrasena.matches(REGEX_CONTRASENA)) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, " +
                    "una letra, un número y un carácter especial (.!@#$%&*-_).");
        }

        if (usuarioActualizado.getTelefono() == null ||
                !usuarioActualizado.getTelefono().matches(REGEX_TELEFONO)) {
            throw new IllegalArgumentException(
                    "El teléfono debe tener exactamente 10 dígitos numéricos.");
        }

        if (nuevaContrasena.equals(contrasenaActual)) {
            throw new IllegalArgumentException(
                    "La nueva contraseña no puede ser igual a la actual.");
        }

        repository.modificar(usuarioActualizado, usuarioActualizado.getId_usuario());
    }
}