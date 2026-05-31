package com.saberpro.usecases.rol;

import com.saberpro.entities.Roles;
import com.saberpro.usecases.ports.RolesRepository;

import java.sql.SQLException;

public class BuscarRolUseCase {

    private final RolesRepository repository;

    public BuscarRolUseCase(RolesRepository repository) {
        this.repository = repository;
    }

    public Roles ejecutar(int idRol) throws SQLException {
        Roles rol = new Roles();
        rol.setId_roles(idRol);

        boolean encontrado = repository.buscar(rol);

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No se encontró un rol con el ID indicado.");
        }

        return rol;
    }
}