package saberPro.infrastructure.persistence.repositories;

import saberPro.entities.Roles;
import saberPro.infrastructure.config.PostgresConexion;
import saberPro.usecases.ports.RolesRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolesRepositoryImpl implements RolesRepository {

    private final PostgresConexion conexion = new PostgresConexion();

    @Override
    public boolean registrar(Roles rol) throws SQLException {
        String sql = "INSERT INTO roles (nombre) VALUES (?)";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol.getNombre());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean editar(Roles rol, int idOriginal) throws SQLException {
        String sql = "UPDATE roles SET id_roles=?, nombre=? WHERE id_roles=?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_roles());
            ps.setString(2, rol.getNombre());
            ps.setInt(3, idOriginal);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(Roles rol) throws SQLException {
        String sql = "DELETE FROM roles WHERE id_roles=?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_roles());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean buscar(Roles rol) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id_roles=?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_roles());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rol.setId_roles(rs.getInt("id_roles"));
                rol.setNombre(rs.getString("nombre"));
                return true;
            }
            return false;
        }
    }

    @Override
    public List<Roles> listarTodos() throws SQLException {
        String sql = "SELECT id_roles, nombre FROM roles ORDER BY nombre";
        List<Roles> lista = new ArrayList<>();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                lista.add(new Roles(rs.getInt("id_roles"), rs.getString("nombre")));
        }
        return lista;
    }
}