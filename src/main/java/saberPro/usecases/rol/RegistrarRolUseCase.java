package saberPro.usecases.rol;

import saberPro.entities.Roles;
import saberPro.usecases.ports.RolesRepository;

import java.sql.SQLException;

public class RegistrarRolUseCase {

    private final RolesRepository repository;

    public RegistrarRolUseCase(RolesRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(Roles rol) throws SQLException {
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del rol no puede estar vacío.");
        }

        repository.registrar(rol);
    }
}