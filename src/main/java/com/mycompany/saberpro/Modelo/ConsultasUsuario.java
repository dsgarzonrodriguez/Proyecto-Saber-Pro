/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author juanf
 */
public class ConsultasUsuario extends Conexion {

    public boolean login(Usuario u) {
        String sql = "SELECT id_usuario, correo, contrasena, id_roles, habilitado "
                + "FROM usuario "
                + "WHERE correo = ? AND contrasena = ?";

        try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getContrasena());

            // DEBUG: para ver exactamente qué se está mandando
            System.out.println("DEBUG login -> " + ps);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Estos SÍ existen en la tabla usuario
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setHabilitado(rs.getBoolean("habilitado"));

                // Si necesitas el rol, al menos el id:
                Roles rol = new Roles();
                rol.setId_roles(rs.getInt("id_roles"));
                u.setRol(rol);

                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al intentar iniciar sesión:\n" + e.getMessage());
            return false;
        }
    }

    public ResultSet obtenerRoles() {
        Connection con = getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT DISTINCT rol FROM vista_usuarios ORDER BY rol"
            );
            return ps.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error obtenerRoles: " + e.getMessage());
            return null;
        }
    }

    // Versión antigua (compatibilidad): por defecto solo trae el rol actual
    public ResultSet consultarUsuarios(String texto, String rol, String estado,
            java.sql.Timestamp inicio, java.sql.Timestamp fin) {
        return consultarUsuarios(texto, rol, estado, inicio, fin, true);
    }

// Nueva versión con bandera "soloActual"
    public ResultSet consultarUsuarios(String texto, String rol, String estado,
            java.sql.Timestamp inicio, java.sql.Timestamp fin,
            boolean soloActual) {
        Connection con = getConexion();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT nombre, apellido, telefono, cc, correo, habilitado, rol, fecha_inicio, fecha_fin ")
                    .append("FROM vista_usuarios WHERE 1=1 ");

            // 👇 SOLO agrego este filtro si quiero solo el rol vigente
            if (soloActual) {
                sql.append("AND fecha_fin IS NULL ");
            }

            // FILTRO por estado
            if (estado != null && !estado.equalsIgnoreCase("Todos")) {
                sql.append("AND trim(lower(habilitado)) = trim(lower('" + estado + "')) ");
            }

            if (texto != null && !texto.trim().isEmpty()) {
                sql.append("AND (LOWER(nombre) LIKE LOWER(?) OR LOWER(apellido) LIKE LOWER(?) ")
                        .append("OR telefono LIKE ? OR cc LIKE ? OR LOWER(correo) LIKE LOWER(?)) ");
            }

            if (rol != null && !rol.equalsIgnoreCase("Todos")) {
                sql.append("AND LOWER(rol) = LOWER(?) ");
            }

            if (inicio != null) {
                sql.append("AND fecha_inicio >= ? ");
            }
            if (fin != null) {
                sql.append("AND fecha_fin <= ? ");
            }

            ps = con.prepareStatement(sql.toString());

            int index = 1;
            if (texto != null && !texto.trim().isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    ps.setString(index++, "%" + texto.trim() + "%");
                }
            }
            if (rol != null && !rol.equalsIgnoreCase("Todos")) {
                ps.setString(index++, rol);
            }
            if (inicio != null) {
                ps.setTimestamp(index++, inicio);
            }
            if (fin != null) {
                ps.setTimestamp(index++, fin);
            }

            System.out.println("DEBUG SQL consultarUsuarios: " + ps);

            rs = ps.executeQuery();

        } catch (SQLException e) {
            System.err.println("Error en consultarUsuarios: " + e.getMessage());
        }

        return rs;
    }

    // ========================
    public boolean registrar(Usuario usr) {
        PreparedStatement ps = null;
        Connection con = getConexion();
        ResultSet rs = null;

        try {
            // 🔹 Verificar que el correo no esté repetido en usuario
            String checkCorreoSQL = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkCorreoSQL);
            checkStmt.setString(1, usr.getCorreo());
            ResultSet rsCheck = checkStmt.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "El correo ya está registrado.");
                return false;
            }

            // 🔹 Verificar que la CC no esté repetida en NINGÚN rol
            String checkCcSQL = "SELECT COUNT(*) FROM vista_cc_global WHERE cc = ?";
            PreparedStatement checkCcStmt = con.prepareStatement(checkCcSQL);
            checkCcStmt.setString(1, usr.getCc());
            ResultSet rsCc = checkCcStmt.executeQuery();
            if (rsCc.next() && rsCc.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                        "La cédula ingresada ya está registrada en el sistema.");
                return false;
            }

            // 🔹 Validar contraseña
            if (!usr.getContrasena().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$")) {
                JOptionPane.showMessageDialog(null,
                        "La contraseña debe tener al menos 8 caracteres, una letra, un número y un carácter especial permitido (.!@#$%&*-_).");
                return false;
            }

            // 🔹 Validar teléfono
            if (usr.getTelefono() == null || !usr.getTelefono().matches("\\d{10}")) {
                JOptionPane.showMessageDialog(null, "El teléfono debe tener exactamente 10 dígitos numéricos.");
                return false;
            }

            // ==============================
            // 🔹 Insertar en tabla usuario
            // ==============================
            String sqlUsuario = "INSERT INTO usuario (correo, contrasena, id_roles, habilitado) VALUES (?, ?, ?, TRUE)";
            ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usr.getCorreo());
            ps.setString(2, usr.getContrasena());
            ps.setInt(3, usr.getRol().getId_roles());
            ps.executeUpdate();

            // Obtener el ID generado del usuario
            rs = ps.getGeneratedKeys();
            int idUsuario = 0;
            if (rs.next()) {
                idUsuario = rs.getInt(1);
            }

            // ==============================
            // 🔹 Insertar en tabla del rol
            // ==============================
            String sqlRol = "";
            switch (usr.getRol().getNombre().toLowerCase()) {
                case "administrador":
                    sqlRol = "INSERT INTO administrador (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "profesor":
                    sqlRol = "INSERT INTO profesor (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "decano":
                    sqlRol = "INSERT INTO decano (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "comite de programa":
                    sqlRol = "INSERT INTO comite_programa (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "director de programa":
                    sqlRol = "INSERT INTO director_programa (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "coordinador saber pro":
                    sqlRol = "INSERT INTO coordinador_saberpro (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "secretaria de acreditacion":
                    sqlRol = "INSERT INTO secretaria_acreditacion (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "estudiante":
                    sqlRol = "INSERT INTO estudiante (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    break;
                default:
                    throw new SQLException("Rol no reconocido: " + usr.getRol().getNombre());
            }

            ps = con.prepareStatement(sqlRol);
            ps.setString(1, usr.getNombre());
            ps.setString(2, usr.getApellido());
            ps.setString(3, usr.getTelefono());
            ps.setString(4, usr.getCc());
            ps.setInt(5, idUsuario);
            ps.executeUpdate();

            // ==============================
            // 🔹 Insertar registro en HISTORIAL
            // ==============================
            String sqlHistorial = "INSERT INTO historial (id_usuario, id_roles, fecha_inicio) VALUES (?, ?, NOW())";
            ps = con.prepareStatement(sqlHistorial);
            ps.setInt(1, idUsuario);
            ps.setInt(2, usr.getRol().getId_roles());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Usuario registrado correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error en registrar(): " + e);
            JOptionPane.showMessageDialog(null, "Error al guardar el usuario: " + e.getMessage());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    // ========================
    // MÉTODO MODIFICAR USUARIO
    // ========================
    public boolean modificar(Usuario usr, int idOriginal) {
        PreparedStatement ps = null;
        Connection con = getConexion();

        String sqlUsuario = "UPDATE usuario SET correo=?, contrasena=?, id_roles=? WHERE id_usuario=?";

        try {
            // ==========================================
            // 🔹 1) Validar CORREO no repetido (otro user)
            // ==========================================
            String checkCorreoSQL = "SELECT COUNT(*) FROM usuario "
                    + "WHERE correo = ? AND id_usuario <> ?";
            PreparedStatement checkCorreoStmt = con.prepareStatement(checkCorreoSQL);
            checkCorreoStmt.setString(1, usr.getCorreo());
            checkCorreoStmt.setInt(2, idOriginal);
            ResultSet rsCorreo = checkCorreoStmt.executeQuery();
            if (rsCorreo.next() && rsCorreo.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                        "El correo ingresado ya está registrado para otro usuario.");
                return false;
            }

            // ==========================================
            // 🔹 2) Validar CC no repetida (otro user)
            // ==========================================
            String checkCcSQL = "SELECT COUNT(*) FROM vista_cc_global "
                    + "WHERE cc = ? AND id_usuario <> ?";
            PreparedStatement checkCcStmt = con.prepareStatement(checkCcSQL);
            checkCcStmt.setString(1, usr.getCc());
            checkCcStmt.setInt(2, idOriginal);
            ResultSet rsCc = checkCcStmt.executeQuery();
            if (rsCc.next() && rsCc.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                        "La cédula ingresada ya está registrada para otro usuario.");
                return false;
            }

            // ==========================================
            // 🔹 3) Obtener el rol anterior (para historial)
            // ==========================================
            String sqlCheckRol = "SELECT id_roles FROM usuario WHERE id_usuario = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheckRol);
            psCheck.setInt(1, idOriginal);
            ResultSet rsCheck = psCheck.executeQuery();

            int rolAnterior = -1;
            if (rsCheck.next()) {
                rolAnterior = rsCheck.getInt("id_roles");
            }

            // ==========================================
            // 🔹 4) Actualizar tabla usuario
            // ==========================================
            ps = con.prepareStatement(sqlUsuario);
            ps.setString(1, usr.getCorreo());
            ps.setString(2, usr.getContrasena());
            ps.setInt(3, usr.getRol().getId_roles());
            ps.setInt(4, idOriginal);
            ps.executeUpdate();

            // ==========================================
            // 🔹 5) Determinar tabla del rol destino
            // ==========================================
            String nombreRol = usr.getRol().getNombre();
            String rolLower = nombreRol.toLowerCase();
            rolLower = rolLower
                    .replace("á", "a")
                    .replace("é", "e")
                    .replace("í", "i")
                    .replace("ó", "o")
                    .replace("ú", "u");

            String tablaRol = null;
            switch (rolLower) {
                case "administrador":
                    tablaRol = "administrador";
                    break;
                case "estudiante":
                    tablaRol = "estudiante";
                    break;
                case "profesor":
                    tablaRol = "profesor";
                    break;
                case "decano":
                    tablaRol = "decano";
                    break;
                case "comite de programa":
                    tablaRol = "comite_programa";
                    break;
                case "director de programa":
                    tablaRol = "director_programa";
                    break;
                case "coordinador saber pro":
                    tablaRol = "coordinador_saberpro";
                    break;
                case "secretaria de acreditacion":
                    tablaRol = "secretaria_acreditacion";
                    break;
                default:
                    throw new SQLException("Rol no reconocido: " + nombreRol);
            }

            // ==========================================
            // 🔹 6) Ver si ya existe registro en la tabla de ese rol
            // ==========================================
            String sqlExiste = "SELECT COUNT(*) FROM " + tablaRol + " WHERE id_usuario = ?";
            ps = con.prepareStatement(sqlExiste);
            ps.setInt(1, idOriginal);
            ResultSet rsExiste = ps.executeQuery();

            boolean existe = false;
            if (rsExiste.next() && rsExiste.getInt(1) > 0) {
                existe = true;
            }

            if (existe) {
                // 6.a) Ya existe → UPDATE
                String sqlUpdateRol = "UPDATE " + tablaRol
                        + " SET nombre=?, apellido=?, telefono=?, cc=? WHERE id_usuario=?";
                ps = con.prepareStatement(sqlUpdateRol);
                ps.setString(1, usr.getNombre());
                ps.setString(2, usr.getApellido());
                ps.setString(3, usr.getTelefono());
                ps.setString(4, usr.getCc());
                ps.setInt(5, idOriginal);
                ps.executeUpdate();
            } else {
                // 6.b) No existe → INSERT
                String sqlInsertRol = "INSERT INTO " + tablaRol
                        + " (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)";
                ps = con.prepareStatement(sqlInsertRol);
                ps.setString(1, usr.getNombre());
                ps.setString(2, usr.getApellido());
                ps.setString(3, usr.getTelefono());
                ps.setString(4, usr.getCc());
                ps.setInt(5, idOriginal);
                ps.executeUpdate();
            }

            // ==========================================
            // 🔹 7) Actualizar historial si el rol cambió
            // ==========================================
            if (rolAnterior != usr.getRol().getId_roles()) {
                // Cerrar registro anterior
                String sqlCerrarHistorial = "UPDATE historial SET fecha_fin = NOW() "
                        + "WHERE id_usuario = ? AND fecha_fin IS NULL";
                ps = con.prepareStatement(sqlCerrarHistorial);
                ps.setInt(1, idOriginal);
                ps.executeUpdate();

                // Crear nuevo registro de historial
                String sqlNuevoHistorial = "INSERT INTO historial (id_usuario, id_roles, fecha_inicio) "
                        + "VALUES (?, ?, NOW())";
                ps = con.prepareStatement(sqlNuevoHistorial);
                ps.setInt(1, idOriginal);
                ps.setInt(2, usr.getRol().getId_roles());
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e);
            JOptionPane.showMessageDialog(null, "Error al modificar usuario: " + e.getMessage());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public boolean eliminar(Usuario usr) {
        PreparedStatement ps = null;
        Connection con = getConexion();

        try {
            String checkSQL = "SELECT habilitado FROM usuario WHERE id_usuario = ?";
            ps = con.prepareStatement(checkSQL);
            ps.setInt(1, usr.getId_usuario());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean habilitado = rs.getBoolean("habilitado");
                if (!habilitado) {
                    JOptionPane.showMessageDialog(null, "Este usuario ya está deshabilitado.");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "El usuario no existe.");
                return false;
            }

            String sqlUpdate = "UPDATE usuario SET habilitado = FALSE WHERE id_usuario = ?";
            ps = con.prepareStatement(sqlUpdate);
            ps.setInt(1, usr.getId_usuario());
            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "El usuario fue inhabilitado correctamente.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo inhabilitar el usuario.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al inhabilitar usuario: " + e);
            JOptionPane.showMessageDialog(null, "Error al intentar inhabilitar usuario: " + e.getMessage());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public boolean buscar(Usuario usr) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = getConexion();

        try {
            //Determinar si buscar por ID o por correo
            String sqlBase = "SELECT u.id_usuario, u.correo, u.contrasena, u.habilitado, r.id_roles, r.nombre AS nombre_rol "
                    + "FROM usuario u INNER JOIN roles r ON u.id_roles = r.id_roles ";

            String whereClause = "";

            if (usr.getId_usuario() > 0) {
                whereClause = "WHERE u.id_usuario=?";
            } else if (usr.getCorreo() != null && !usr.getCorreo().isEmpty()) {
                whereClause = "WHERE u.correo=?";
            } else {
                System.out.println("No se proporcionó ni ID ni correo para la búsqueda.");
                return false;
            }

            ps = con.prepareStatement(sqlBase + whereClause);

            // Asignar parámetro dependiendo del tipo de búsqueda
            if (usr.getId_usuario() > 0) {
                ps.setInt(1, usr.getId_usuario());
            } else {
                ps.setString(1, usr.getCorreo());
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                usr.setId_usuario(rs.getInt("id_usuario"));
                usr.setCorreo(rs.getString("correo"));
                usr.setContrasena(rs.getString("contrasena"));
                usr.setHabilitado(rs.getBoolean("habilitado"));

                Roles rol = new Roles();
                rol.setId_roles(rs.getInt("id_roles"));
                rol.setNombre(rs.getString("nombre_rol"));
                usr.setRol(rol);

                // Buscar los datos personales según el rol
                String sqlRol = "";
                switch (rol.getNombre().toLowerCase()) {
                    case "administrador":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM administrador WHERE id_usuario=?";
                        break;
                    case "estudiante":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM estudiante WHERE id_usuario=?";
                        break;
                    case "profesor":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM profesor WHERE id_usuario=?";
                        break;
                    case "decano":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM decano WHERE id_usuario=?";
                        break;
                    case "comite de programa":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM comite_programa WHERE id_usuario=?";
                        break;
                    case "director de programa":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM director_programa WHERE id_usuario=?";
                        break;
                    case "coordinador saber pro":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM coordinador_saberpro WHERE id_usuario=?";
                        break;
                    case "secretaria de acreditacion":
                        sqlRol = "SELECT nombre, apellido, telefono, cc FROM secretaria_acreditacion WHERE id_usuario=?";
                        break;
                }

                ps = con.prepareStatement(sqlRol);
                ps.setInt(1, usr.getId_usuario());
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("DEBUG: nombre=" + rs.getString("nombre"));
                    System.out.println("DEBUG: apellido=" + rs.getString("apellido"));
                    System.out.println("DEBUG: telefono=" + rs.getString("telefono"));
                    System.out.println("DEBUG: cc=" + rs.getString("cc"));

                    usr.setNombre(rs.getString("nombre"));
                    usr.setApellido(rs.getString("apellido"));
                    usr.setTelefono(rs.getString("telefono"));
                    usr.setCc(rs.getString("cc"));
                }

                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public int obtenerIdRolPorNombre(String nombreRol) {
        int id = -1;
        String sql = "SELECT id_roles FROM roles WHERE LOWER(nombre) = LOWER(?)";
        try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreRol);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_roles");
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerIdRolPorNombre: " + e.getMessage());
        }
        return id;
    }

    public boolean habilitar(Usuario usr) {
        PreparedStatement ps = null;
        Connection con = getConexion();

        try {
            String checkSQL = "SELECT habilitado FROM usuario WHERE id_usuario = ?";
            ps = con.prepareStatement(checkSQL);
            ps.setInt(1, usr.getId_usuario());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean habilitado = rs.getBoolean("habilitado");
                if (habilitado) {
                    JOptionPane.showMessageDialog(null, "Este usuario ya está habilitado.");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "El usuario no existe.");
                return false;
            }

            String sqlUpdate = "UPDATE usuario SET habilitado = TRUE WHERE id_usuario = ?";
            ps = con.prepareStatement(sqlUpdate);
            ps.setInt(1, usr.getId_usuario());
            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "El usuario fue habilitado correctamente.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo habilitar el usuario.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al habilitar usuario: " + e);
            JOptionPane.showMessageDialog(null, "Error al intentar habilitar usuario: " + e.getMessage());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}
