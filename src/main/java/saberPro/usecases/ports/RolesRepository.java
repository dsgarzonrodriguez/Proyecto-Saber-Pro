package saberPro.usecases.ports;

import saberPro.entities.Roles;

import java.sql.SQLException;
import java.util.List;

public interface RolesRepository {

    boolean registrar(Roles rol) throws SQLException;
    boolean editar(Roles rol, int idOriginal) throws SQLException;
    boolean eliminar(Roles rol) throws SQLException;
    boolean buscar(Roles rol) throws SQLException;
    List<Roles> listarTodos() throws SQLException;
}