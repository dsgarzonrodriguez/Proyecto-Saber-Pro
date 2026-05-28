package saberPro.infrastructure.persistence.repositories;

import saberPro.entities.Roles;
import saberPro.entities.Usuario;
import saberPro.infrastructure.config.PostgresConexion;
import saberPro.usecases.ports.UsuarioRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final PostgresConexion conexion = new PostgresConexion();

    @Override
    public boolean login(Usuario u) throws SQLException {
        String sql = "SELECT u.id_usuario, u.correo, u.contrasena, u.id_roles, u.habilitado, r.nombre " +
                    "FROM usuario u JOIN roles r ON u.id_roles = r.id_roles " +
                    "WHERE u.correo = ? AND u.contrasena = ?";
        try (Connection con = conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getContrasena());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setHabilitado(rs.getBoolean("habilitado"));
                Roles rol = new Roles();
                rol.setId_roles(rs.getInt("id_roles"));
                rol.setNombre(rs.getString("nombre")); // <-- esto faltaba
                u.setRol(rol);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean registrar(Usuario usr) throws SQLException {
        try (Connection con = conexion.getConexion()) {

            // Verificar correo duplicado
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM usuario WHERE correo = ?")) {
                ps.setString(1, usr.getCorreo());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0)
                    throw new SQLException("El correo ya está registrado.");
            }

            // Verificar CC duplicada en vista_cc_global
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM vista_cc_global WHERE cc = ?")) {
                ps.setString(1, usr.getCc());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0)
                    throw new SQLException("La cédula ya está registrada.");
            }

            // Insertar en usuario
            int idUsuario;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO usuario (correo, contrasena, id_roles, habilitado) " +
                    "VALUES (?, ?, ?, TRUE)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usr.getCorreo());
                ps.setString(2, usr.getContrasena());
                ps.setInt(3, usr.getRol().getId_roles());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("No se generó el ID de usuario.");
                idUsuario = rs.getInt(1);
            }

            // Insertar en tabla del rol
            String tablaRol = resolverTablaRol(usr.getRol().getNombre());
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO " + tablaRol +
                    " (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, usr.getNombre());
                ps.setString(2, usr.getApellido());
                ps.setString(3, usr.getTelefono());
                ps.setString(4, usr.getCc());
                ps.setInt(5, idUsuario);
                ps.executeUpdate();
            }

            // Insertar en historial
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO historial (id_usuario, id_roles, fecha_inicio) " +
                    "VALUES (?, ?, NOW())")) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, usr.getRol().getId_roles());
                ps.executeUpdate();
            }

            return true;
        }
    }

    @Override
    public boolean modificar(Usuario usr, int idOriginal) throws SQLException {
        try (Connection con = conexion.getConexion()) {

            // Obtener rol anterior
            int rolAnterior;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_roles FROM usuario WHERE id_usuario = ?")) {
                ps.setInt(1, idOriginal);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new SQLException("Usuario no encontrado.");
                rolAnterior = rs.getInt("id_roles");
            }

            // Actualizar usuario
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE usuario SET correo=?, contrasena=?, id_roles=? " +
                    "WHERE id_usuario=?")) {
                ps.setString(1, usr.getCorreo());
                ps.setString(2, usr.getContrasena());
                ps.setInt(3, usr.getRol().getId_roles());
                ps.setInt(4, idOriginal);
                ps.executeUpdate();
            }

            // Actualizar tabla del rol
            String tablaRol = resolverTablaRol(usr.getRol().getNombre());
            boolean existe;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM " + tablaRol + " WHERE id_usuario = ?")) {
                ps.setInt(1, idOriginal);
                ResultSet rs = ps.executeQuery();
                existe = rs.next() && rs.getInt(1) > 0;
            }

            String sqlRol = existe
                ? "UPDATE " + tablaRol + " SET nombre=?, apellido=?, telefono=?, cc=? WHERE id_usuario=?"
                : "INSERT INTO " + tablaRol + " (nombre, apellido, telefono, cc, id_usuario) VALUES (?,?,?,?,?)";

            try (PreparedStatement ps = con.prepareStatement(sqlRol)) {
                ps.setString(1, usr.getNombre());
                ps.setString(2, usr.getApellido());
                ps.setString(3, usr.getTelefono());
                ps.setString(4, usr.getCc());
                ps.setInt(5, idOriginal);
                ps.executeUpdate();
            }

            // Actualizar historial si cambió el rol
            if (rolAnterior != usr.getRol().getId_roles()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE historial SET fecha_fin = NOW() " +
                        "WHERE id_usuario = ? AND fecha_fin IS NULL")) {
                    ps.setInt(1, idOriginal);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO historial (id_usuario, id_roles, fecha_inicio) VALUES (?, ?, NOW())")) {
                    ps.setInt(1, idOriginal);
                    ps.setInt(2, usr.getRol().getId_roles());
                    ps.executeUpdate();
                }
            }

            return true;
        }
    }

    @Override
    public boolean eliminar(Usuario usr) throws SQLException {
        String sql = "UPDATE usuario SET habilitado = FALSE WHERE id_usuario = ?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usr.getId_usuario());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean habilitar(Usuario usr) throws SQLException {
        String sql = "UPDATE usuario SET habilitado = TRUE WHERE id_usuario = ?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usr.getId_usuario());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean buscar(Usuario usr) throws SQLException {
        String sql = "SELECT u.id_usuario, u.correo, u.contrasena, u.habilitado, " +
                     "r.id_roles, r.nombre AS nombre_rol " +
                     "FROM usuario u JOIN roles r ON u.id_roles = r.id_roles " +
                     "WHERE " + (usr.getId_usuario() > 0 ? "u.id_usuario=?" : "u.correo=?");

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (usr.getId_usuario() > 0) ps.setInt(1, usr.getId_usuario());
            else ps.setString(1, usr.getCorreo());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;

            usr.setId_usuario(rs.getInt("id_usuario"));
            usr.setCorreo(rs.getString("correo"));
            usr.setContrasena(rs.getString("contrasena"));
            usr.setHabilitado(rs.getBoolean("habilitado"));

            Roles rol = new Roles(rs.getInt("id_roles"), rs.getString("nombre_rol"));
            usr.setRol(rol);

            String tablaRol = resolverTablaRol(rol.getNombre());
            try (PreparedStatement ps2 = con.prepareStatement(
                    "SELECT nombre, apellido, telefono, cc FROM " + tablaRol +
                    " WHERE id_usuario=?")) {
                ps2.setInt(1, usr.getId_usuario());
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    usr.setNombre(rs2.getString("nombre"));
                    usr.setApellido(rs2.getString("apellido"));
                    usr.setTelefono(rs2.getString("telefono"));
                    usr.setCc(rs2.getString("cc"));
                }
            }
            return true;
        }
    }

    @Override
    public List<Usuario> consultarUsuarios(String texto, String rol, String estado, String inicio, String fin) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT nombre, apellido, telefono, cc, correo, habilitado, " +
            "rol, fecha_inicio, fecha_fin FROM vista_usuarios WHERE fecha_fin IS NULL ");

        List<Object> params = new ArrayList<>();

        if (estado != null && !estado.equalsIgnoreCase("Todos")) {
            sql.append("AND LOWER(habilitado) = LOWER(?) ");
            params.add(estado);
        }
        if (texto != null && !texto.trim().isEmpty()) {
            sql.append("AND (LOWER(nombre) LIKE LOWER(?) OR LOWER(apellido) LIKE LOWER(?) " +
                       "OR telefono LIKE ? OR cc LIKE ? OR LOWER(correo) LIKE LOWER(?)) ");
            String p = "%" + texto.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(p);
        }
        if (rol != null && !rol.equalsIgnoreCase("Todos")) {
            sql.append("AND LOWER(rol) = LOWER(?) ");
            params.add(rol);
        }

        List<Usuario> lista = new ArrayList<>();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setTelefono(rs.getString("telefono"));
                u.setCc(rs.getString("cc"));
                u.setCorreo(rs.getString("correo"));
                u.setHabilitado(rs.getString("habilitado").equals("Habilitado"));
                Roles r = new Roles();
                r.setNombre(rs.getString("rol"));
                u.setRol(r);
                lista.add(u);
            }
        }
        return lista;
    }

    @Override
    public boolean verificarCorreoExiste(String correo) throws SQLException {
        String sql = "SELECT habilitado FROM usuario WHERE correo = ?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            if (!rs.getBoolean("habilitado"))
                throw new IllegalStateException(
                        "La cuenta asociada a este correo está deshabilitada. " +
                        "Por favor contacte al administrador del sistema.");
            return true;
        }
    }

    @Override
    public boolean cambiarContrasena(String correo, String nuevaContrasena) throws SQLException {
        String sql = "UPDATE usuario SET contrasena = ? WHERE correo = ?";
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setString(2, correo);
            return ps.executeUpdate() > 0;
        }
    }

    private String resolverTablaRol(String nombreRol) throws SQLException {
        String normalizado = nombreRol.toLowerCase()
            .replace("á","a").replace("é","e")
            .replace("í","i").replace("ó","o").replace("ú","u");
        return switch (normalizado) {
            case "administrador"              -> "administrador";
            case "estudiante"                 -> "estudiante";
            case "egresado"                   -> "estudiante";
            case "profesor"                   -> "profesor";
            case "decano"                     -> "decano";
            case "comite de programa"         -> "comite_programa";
            case "director de programa"       -> "director_programa";
            case "coordinador saber pro"      -> "coordinador_saberpro";
            case "secretaria de acreditacion" -> "secretaria_acreditacion";
            default -> throw new SQLException("Rol no reconocido: " + nombreRol);
        };
    }
}