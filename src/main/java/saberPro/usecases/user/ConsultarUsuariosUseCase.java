package saberPro.usecases.user;

import saberPro.entities.Usuario;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.SQLException;
import java.util.List;

public class ConsultarUsuariosUseCase {

    private final UsuarioRepository repository;

    public ConsultarUsuariosUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> ejecutar(String texto, String rol,
                                  String estado, String inicio, String fin)
            throws SQLException {
        return repository.consultarUsuarios(texto, rol, estado, inicio, fin);
    }
}