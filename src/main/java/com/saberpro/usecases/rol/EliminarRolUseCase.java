package com.saberpro.usecases.rol;

import com.saberpro.entities.Roles;
import com.saberpro.usecases.ports.RolesRepository;

import java.sql.SQLException;

public class EliminarRolUseCase {

    private final RolesRepository repository;

    public EliminarRolUseCase(RolesRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(int idRol) throws SQLException {
        Roles rol = new Roles();
        rol.setId_roles(idRol);

        boolean encontrado = repository.buscar(rol);

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No se encontró un rol con el ID indicado.");
        }

        repository.eliminar(rol);
    }
}