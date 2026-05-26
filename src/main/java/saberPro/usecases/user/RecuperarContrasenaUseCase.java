package saberPro.usecases.user;

import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;

public class RecuperarContrasenaUseCase {

    private static final String REGEX_CONTRASENA =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$";

    private final UsuarioRepository repository;

    public RecuperarContrasenaUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(String correo, String nuevaContrasena, String repetirContrasena) throws SQLException {
        if (nuevaContrasena == null || !nuevaContrasena.equals(repetirContrasena)) {
            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden.");
        }

        if (!nuevaContrasena.matches(REGEX_CONTRASENA)) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, " +
                    "una letra, un número y un carácter especial (.!@#$%&*-_).");
        }

        boolean actualizado = repository.cambiarContrasena(correo, nuevaContrasena);

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No se encontró un usuario con ese correo.");
        }
    }
}