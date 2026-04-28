/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Modelo.Conexion;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author juanf
 */
public class InterCargaResultados extends javax.swing.JInternalFrame {

    /**
     * Creates new form InterCargaResultados
     */
    public InterCargaResultados() {
        initComponents();
        this.setSize(new Dimension(520, 189));
        this.setTitle("CARGAR MASIVA DE RESULTADOS");
    }

    private String generarContrasena() {
        // Genera una contraseña parecida a las de Google
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            pass.append(chars.charAt(index));
        }

        return pass.toString();

    }

    private void cargarEstudiante(Connection conn, String[] campos) throws SQLException {
        String nombre = campos[0].trim();
        String apellido = campos[1].trim();
        String telefono = campos[2].trim();
        String cc = campos[3].trim();
        String correo = campos[4].trim();

        // Verificar si ya existe el usuario
        PreparedStatement psBuscar = conn.prepareStatement(
                "SELECT u.id_usuario, e.id_estudiante FROM usuario u "
                + "JOIN estudiante e ON e.id_usuario = u.id_usuario "
                + "WHERE u.correo = ? AND e.nombre = ? AND e.apellido = ? AND e.cc = ?"
        );
        psBuscar.setString(1, correo);
        psBuscar.setString(2, nombre);
        psBuscar.setString(3, apellido);
        psBuscar.setString(4, cc);
        ResultSet rs = psBuscar.executeQuery();

        if (rs.next()) {
            int idExistente = rs.getInt("id_estudiante");
            System.out.println("Estudiante existente con id: " + idExistente);
            rs.close();
            psBuscar.close();
            return;
        }
        rs.close();
        psBuscar.close();

        String contrasena = generarContrasena();
        int idRolEstudiante = 4;

        PreparedStatement psUsuario = conn.prepareStatement(
                "INSERT INTO usuario (correo, contrasena, id_roles, habilitado) "
                + "VALUES (?, ?, ?, TRUE) RETURNING id_usuario"
        );
        psUsuario.setString(1, correo);
        psUsuario.setString(2, contrasena);
        psUsuario.setInt(3, idRolEstudiante);
        ResultSet rsUsuario = psUsuario.executeQuery();

        int idUsuario = 0;
        if (rsUsuario.next()) {
            idUsuario = rsUsuario.getInt("id_usuario");
        }
        rsUsuario.close();
        psUsuario.close();

        PreparedStatement psEst = conn.prepareStatement(
                "INSERT INTO estudiante (nombre, apellido, telefono, cc, id_usuario) VALUES (?, ?, ?, ?, ?)");
        psEst.setString(1, nombre);
        psEst.setString(2, apellido);
        psEst.setString(3, telefono);
        psEst.setString(4, cc);
        psEst.setInt(5, idUsuario);
        psEst.executeUpdate();
        psEst.close();
    }

    private void cargarRegistro(Connection conn, String[] campos) throws SQLException {
        String numeroRegistro = campos[0].trim();   // número_de_registro
        String tipoEvaluado = campos[1].trim();     // tipo_de_evaluado
        String documento = campos[2].trim();        // cc del estudiante
        int ano = Integer.parseInt(campos[3].trim());
        int semestre = Integer.parseInt(campos[4].trim());

        // Buscar el id_estudiante a partir del documento
        int idEstudiante = 0;
        try (PreparedStatement psBuscar = conn.prepareStatement(
                "SELECT id_estudiante FROM estudiante WHERE cc = ?")) {
            psBuscar.setString(1, documento);
            try (ResultSet rs = psBuscar.executeQuery()) {
                if (rs.next()) {
                    idEstudiante = rs.getInt("id_estudiante");
                } else {
                    throw new SQLException("No se encontró estudiante con cc: " + documento);
                }
            }
        }

        // Insertar nuevo registro y obtener su id
        int idRegistro = 0;
        String sqlInsertRegistro = """
        INSERT INTO registro (codigo, tipo_evaluado)
        VALUES (?, ?)
        RETURNING id_registro
    """;

        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertRegistro)) {
            psInsert.setString(1, numeroRegistro);
            psInsert.setString(2, tipoEvaluado);
            try (ResultSet rs = psInsert.executeQuery()) {
                if (rs.next()) {
                    idRegistro = rs.getInt("id_registro");
                } else {
                    throw new SQLException("No se pudo obtener el id del nuevo registro");
                }
            }
        }

        // Insertar en prueba
        String sqlInsertPrueba = """
        INSERT INTO prueba (id_estudiante, id_registro, ano, semestre)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement psPrueba = conn.prepareStatement(sqlInsertPrueba)) {
            psPrueba.setInt(1, idEstudiante);
            psPrueba.setInt(2, idRegistro);
            psPrueba.setInt(3, ano);
            psPrueba.setInt(4, semestre);
            psPrueba.executeUpdate();
        }
    }

    private void cargarModulo(Connection conn, String[] campos) throws SQLException {
        String nombre = campos[0].trim();

        PreparedStatement psCheck = conn.prepareStatement("SELECT id_modulos FROM modulos WHERE nombre = ?");
        psCheck.setString(1, nombre);
        ResultSet rs = psCheck.executeQuery();
        if (rs.next()) {
            rs.close();
            psCheck.close();
            return;
        }
        rs.close();
        psCheck.close();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO modulos (nombre) VALUES (?)");
        ps.setString(1, nombre);
        ps.executeUpdate();
        ps.close();
    }

    private void cargarResultadosModulo(Connection conn, String[] campos) throws SQLException {
        String moduloUnico = campos[0].trim();            // viene del CSV
        double puntaje = Double.parseDouble(campos[1].trim());
        String nivel = campos[2].trim();
        double percentil = Double.parseDouble(campos[3].trim());
        String nombreModulo = campos[4].trim();

        int idModulo = buscarId(conn, "modulos", "nombre", nombreModulo, "id_modulos");

        int idResultados;
        String sqlSel = "SELECT id_resultados FROM resultados WHERE modulo_unico = ?";
        try (PreparedStatement psSel = conn.prepareStatement(sqlSel)) {
            psSel.setString(1, moduloUnico);
            try (ResultSet rs = psSel.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No existe resultados con modulo_unico = " + moduloUnico);
                }
                idResultados = rs.getInt(1);
            }
        }

        String sql = """
        INSERT INTO resultados_modulo
          (puntaje_modulo, nivel_desempeno, percentil_nacional_modulo, id_modulos, id_resultados)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, puntaje);
            ps.setString(2, nivel);
            ps.setDouble(3, percentil);
            ps.setInt(4, idModulo);
            ps.setInt(5, idResultados);
            ps.executeUpdate();
        }
    }

    private void cargarCiudad(Connection conn, String[] campos) throws SQLException {
        String nombre = campos[0].trim();

        // Evitar duplicados
        PreparedStatement psCheck = conn.prepareStatement("SELECT id_ciudad FROM ciudad WHERE nombre = ?");
        psCheck.setString(1, nombre);
        ResultSet rs = psCheck.executeQuery();
        if (rs.next()) {
            rs.close();
            psCheck.close();
            return;
        }
        rs.close();
        psCheck.close();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO ciudad (nombre) VALUES (?)");
        ps.setString(1, nombre);
        ps.executeUpdate();
        ps.close();
    }

    private void cargarPrograma(Connection conn, String[] campos) throws SQLException {
        String nombre = campos[0].trim();
        int snies = Integer.parseInt(campos[1].trim()); // convertir a entero

        PreparedStatement psCheck = conn.prepareStatement("SELECT id_programa FROM programa WHERE snies = ?");
        psCheck.setInt(1, snies); // usar setInt, no setString
        ResultSet rs = psCheck.executeQuery();
        if (rs.next()) {
            rs.close();
            psCheck.close();
            return; // ya existe, no insertar de nuevo
        }
        rs.close();
        psCheck.close();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO programa (nombre, snies) VALUES (?, ?)");
        ps.setString(1, nombre);
        ps.setInt(2, snies); // usar setInt aquí también
        ps.executeUpdate();
        ps.close();
    }

    private int buscarId(Connection conn, String tabla, String campoBusqueda, String valor, String campoId) throws SQLException {
        String sql = "SELECT " + campoId + " FROM " + tabla + " WHERE " + campoBusqueda + " = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, valor);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            rs.close();
            ps.close();
            return id;
        } else {
            rs.close();
            ps.close();
            throw new SQLException("No se encontró " + valor + " en " + tabla);
        }
    }

    private void cargarResultados(Connection conn, String[] campos) throws SQLException {
        String moduloUnico = campos[0].trim();           // modulo_unico
        String numeroRegistro = campos[1].trim();           // numero_de_registro
        double puntajeGlobal = Double.parseDouble(campos[2].trim());
        double percentilGlobal = Double.parseDouble(campos[3].trim());
        String nombreCiudad = campos[4].trim();
        String nombrePrograma = campos[5].trim();

        int idCiudad = buscarId(conn, "ciudad", "nombre", nombreCiudad, "id_ciudad");
        int idPrograma = buscarId(conn, "programa", "nombre", nombrePrograma, "id_programa");
        int idRegistro = buscarId(conn, "registro", "codigo", numeroRegistro, "id_registro");

        // Insertar resultados guardando modulo_unico
        String sqlRes = """
        INSERT INTO resultados
          (puntaje_global, porcentaje_nacional_global, id_ciudad, id_programa, id_registro, modulo_unico)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id_resultados
    """;

        int idResultados = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlRes)) {
            ps.setDouble(1, puntajeGlobal);
            ps.setDouble(2, percentilGlobal);
            ps.setInt(3, idCiudad);
            ps.setInt(4, idPrograma);
            ps.setInt(5, idRegistro);
            ps.setString(6, moduloUnico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idResultados = rs.getInt(1);
                }
            }
        }

        // aquí ya no tocamos resultados_modulo; eso se hace en cargarResultadosModulo
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        txtArchivo1 = new javax.swing.JTextField();
        btnCargar1 = new javax.swing.JButton();
        btnExaminar1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Cargua de archivos de resultados:");
        jLabel3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLabel3FocusGained(evt);
            }
        });
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 250, -1));

        txtArchivo1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtArchivo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArchivo1ActionPerformed(evt);
            }
        });
        getContentPane().add(txtArchivo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 180, -1));

        btnCargar1.setBackground(new java.awt.Color(0, 0, 153));
        btnCargar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCargar1.setForeground(new java.awt.Color(255, 255, 255));
        btnCargar1.setText("Cargar");
        btnCargar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargar1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnCargar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, 160, -1));

        btnExaminar1.setBackground(new java.awt.Color(0, 0, 153));
        btnExaminar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnExaminar1.setForeground(new java.awt.Color(255, 255, 255));
        btnExaminar1.setText("Examinar");
        btnExaminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminar1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnExaminar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 90, 180, -1));

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 570, 170));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCargar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargar1ActionPerformed
        String ruta = null;
        try {
            ruta = txtArchivo1.getText(); // asegúrate que txtArchivo1 es el componente correcto
        } catch (Exception ex) {
            // Si por alguna razón la referencia al componente está mal, lo verás aquí
            JOptionPane.showMessageDialog(this, "Error al leer el campo de ruta: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        // Muestra lo que realmente tienes, para que no te quedes pensando que el código es magico
        System.out.println("DEBUG: valor txtArchivo1 = [" + ruta + "]");
        // Si quieres ver un popup:
        // JOptionPane.showMessageDialog(this, "DEBUG: valor txtArchivo1 = [" + ruta + "]");

        // Validación robusta de ruta vacía o null
        if (ruta == null || ruta.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un archivo primero.");
            return;
        }

        File archivo = new File(ruta);
        if (!archivo.exists() || archivo.isDirectory()) {
            JOptionPane.showMessageDialog(this, "El archivo no existe o es un directorio.");
            return;
        }

        String nombreArchivo = archivo.getName().toLowerCase().trim();
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConexion();

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
            return;
        }

        // Lee el CSV de forma segura. Maneja comillas y comas internas.
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine(); // lee cabecera (puede ser null si archivo vacío)
            if (linea == null) {
                JOptionPane.showMessageDialog(this, "El archivo está vacío.");
                conn.close();
                return;
            }

            while ((linea = br.readLine()) != null) {
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Parseo más robusto: respeta comillas
                List<String> camposList = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                boolean dentroComillas = false;
                for (int i = 0; i < linea.length(); i++) {
                    char c = linea.charAt(i);
                    if (c == '"') {
                        dentroComillas = !dentroComillas;
                    } else if (c == ',' && !dentroComillas) {
                        camposList.add(sb.toString().trim());
                        sb.setLength(0);
                    } else {
                        sb.append(c);
                    }
                }
                camposList.add(sb.toString().trim()); // último campo
                String[] campos = camposList.toArray(new String[0]);

                // Llamada al método adecuado según el nombre del archivo
                try {
                    switch (nombreArchivo) {
                        case "estudiantes.csv":
                            cargarEstudiante(conn, campos);
                            break;
                        case "registro.csv":
                            cargarRegistro(conn, campos);
                            break;
                        case "modulos.csv":
                            cargarModulo(conn, campos);
                            break;
                        case "resultados_modulo.csv":
                            cargarResultadosModulo(conn, campos);
                            break;
                        case "ciudad.csv":
                            cargarCiudad(conn, campos);
                            break;
                        case "programa.csv":
                            cargarPrograma(conn, campos);
                            break;
                        case "resultados.csv":
                            cargarResultados(conn, campos);
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Archivo no reconocido: " + nombreArchivo);
                            conn.close();
                            return;
                    }
                } catch (SQLException se) {
                    // Si falla una fila, la registramos y seguimos con las demás (si prefieres parar, lanza la excepción)
                    String errorMsg = "⚠️ Error al procesar línea en el archivo: " + nombreArchivo
                            + "\nLínea: " + linea
                            + "\nCausa: " + se.getMessage();
                    JOptionPane.showMessageDialog(this, errorMsg, "Error SQL", JOptionPane.ERROR_MESSAGE);
                    se.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(this, "Carga de " + nombreArchivo + " completada con éxito.");
            conn.close();

        } catch (FileNotFoundException fnf) {
            JOptionPane.showMessageDialog(this, "Archivo no encontrado: " + fnf.getMessage());
            fnf.printStackTrace();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(this, "Error leyendo el archivo: " + io.getMessage());
            io.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnCargar1ActionPerformed

    private void jLabel3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLabel3FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel3FocusGained

    private void txtArchivo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArchivo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtArchivo1ActionPerformed

    private void btnExaminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExaminar1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();

        // Solo permitir archivos CSV
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
        fileChooser.setFileFilter(filtro);
        fileChooser.setAcceptAllFileFilterUsed(false); // quita "Todos los archivos"

        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            txtArchivo1.setText(archivo.getAbsolutePath());
        }
    }//GEN-LAST:event_btnExaminar1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargar1;
    private javax.swing.JButton btnExaminar1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtArchivo1;
    // End of variables declaration//GEN-END:variables
}
