package saberPro.usecases.user;

import saberPro.entities.Roles;
import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Registra una lista de usuarios provenientes de un Excel/CSV.
 *
 * Reglas de negocio:
 *  - Valida cada fila (campos obligatorios, formato teléfono, formato contraseña).
 *  - Procesa todas las filas y acumula errores fila por fila sin detener el lote.
 *  - Al final devuelve un resumen con filas OK y filas con error.
 */
public class CargaMasivaUsuarioUseCase {

    private static final String REGEX_CONTRASENA =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$";
    private static final String REGEX_TELEFONO = "\\d{10}";

    private final UsuarioRepository repository;

    public CargaMasivaUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * @param usuarios Lista de usuarios parseados desde el Excel.
     *                 Cada Usuario debe tener: nombre, apellido, telefono,
     *                 cc, correo, contrasena y rol (con nombre seteado).
     * @return {@link ResultadoCarga} con el conteo de éxitos y la lista de errores.
     */
    public ResultadoCarga ejecutar(List<Usuario> usuarios) {
        int exitosos = 0;
        List<String> errores = new ArrayList<>();

        for (int i = 0; i < usuarios.size(); i++) {
            int fila = i + 2; // fila 1 = encabezado, datos desde fila 2
            Usuario u = usuarios.get(i);

            try {
                validar(u, fila);
                repository.registrar(u);
                exitosos++;
            } catch (IllegalArgumentException e) {
                errores.add("Fila " + fila + ": " + e.getMessage());
            } catch (SQLException e) {
                // El repositorio lanza SQLException con mensaje de negocio
                // (correo duplicado, CC duplicada, rol no reconocido)
                errores.add("Fila " + fila + ": " + e.getMessage());
            }
        }

        return new ResultadoCarga(exitosos, errores);
    }

    // ── Validaciones de dominio ──────────────────────────────────────────────

    private void validar(Usuario u, int fila) {
        if (vacio(u.getNombre()))
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (vacio(u.getApellido()))
            throw new IllegalArgumentException("El apellido es obligatorio.");
        if (vacio(u.getCc()))
            throw new IllegalArgumentException("La cédula es obligatoria.");
        if (vacio(u.getCorreo()))
            throw new IllegalArgumentException("El correo es obligatorio.");
        if (u.getRol() == null || vacio(u.getRol().getNombre()))
            throw new IllegalArgumentException("El rol es obligatorio.");

        if (u.getTelefono() == null || !u.getTelefono().matches(REGEX_TELEFONO))
            throw new IllegalArgumentException(
                    "El teléfono debe tener exactamente 10 dígitos numéricos.");

        if (u.getContrasena() == null || !u.getContrasena().matches(REGEX_CONTRASENA))
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, " +
                    "una letra, un número y un carácter especial (.!@#$%&*-_).");
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    // ── Objeto de resultado ──────────────────────────────────────────────────

    public static class ResultadoCarga {
        private final int         exitosos;
        private final List<String> errores;

        public ResultadoCarga(int exitosos, List<String> errores) {
            this.exitosos = exitosos;
            this.errores  = errores;
        }

        public int          getExitosos() { return exitosos; }
        public List<String> getErrores()  { return errores; }
        public int          getFallidos()  { return errores.size(); }
        public boolean      hayErrores()   { return !errores.isEmpty(); }
    }
}