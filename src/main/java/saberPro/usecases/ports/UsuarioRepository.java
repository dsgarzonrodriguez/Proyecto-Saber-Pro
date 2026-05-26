package saberPro.usecases.ports;

import saberPro.entities.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {

    boolean login(Usuario usuario) throws SQLException;

    boolean registrar(Usuario usuario) throws SQLException;

    boolean modificar(Usuario usuario, int idOriginal) throws SQLException;

    boolean eliminar(Usuario usuario) throws SQLException;

    boolean habilitar(Usuario usuario) throws SQLException;

    boolean buscar(Usuario usuario) throws SQLException;

    List<Usuario> consultarUsuarios(String texto, String rol, String estado, String inicio, String fin) throws SQLException;

    boolean verificarCorreoExiste(String correo) throws SQLException;

    boolean cambiarContrasena(String correo, String nuevaContrasena) throws SQLException;
}