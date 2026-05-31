package com.saberpro.usecases.rol;

import com.saberpro.entities.Roles;
import com.saberpro.usecases.ports.RolesRepository;

import java.sql.SQLException;

public class EditarRolUseCase {

    private final RolesRepository repository;

    public EditarRolUseCase(RolesRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(Roles rol, int idOriginal) throws SQLException {
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del rol no puede estar vacío.");
        }

        Roles existente = new Roles();
        existente.setId_roles(idOriginal);
        boolean encontrado = repository.buscar(existente);

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No se encontró un rol con el ID indicado.");
        }

        repository.editar(rol, idOriginal);
    }
}