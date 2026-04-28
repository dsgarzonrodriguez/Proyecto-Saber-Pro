/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Modelo.Conexion;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanf
 */
public class Globales extends javax.swing.JPanel {

    private Integer idUsuario;
    private String cc;
    private String numeroRegistro;

    // >>> añade esto <<<
    private PanelPercentil panelPercentil;

    public Globales() {
        // Este se usa sólo para el editor visual de NetBeans
        initComponents();
        // crear y agregar la barra de percentil
        panelPercentil = new PanelPercentil();
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(panelPercentil, java.awt.BorderLayout.CENTER);

        panelPercentil.setPercentil(0);  // valor inicial

    }

    public Globales(Integer idUsuario, String cc, String numeroRegistro) {
        initComponents();
        this.idUsuario = idUsuario;
        this.cc = cc;
        this.numeroRegistro = numeroRegistro;

        // crear y agregar la barra de percentil
        panelPercentil = new PanelPercentil();
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(panelPercentil, java.awt.BorderLayout.CENTER);

        panelPercentil.setPercentil(0);  // mientras carga
        cargarResultados();
    }

    // ==========================
    //   CARGAR DATOS DESDE BD
    // ==========================
    private void cargarResultados() {
        txtGlobal.setText("Cargando...");
        txtPercentil.setText("Cargando...");
        txtEstudiante.setText("Cargando...");

        Double puntaje = null;
        Integer percentil = null;
        String nombre = null;
        String apellido = null;

        try (Connection con = new Conexion().getConexion()) {

            // =======================
            // 1) CONSULTA DIRECTA A LAS TABLAS
            // =======================
            StringBuilder sql = new StringBuilder(
                    "SELECT e.nombre, e.apellido, "
                    + "       rdo.puntaje_global, "
                    + "       rdo.porcentaje_nacional_global AS percentil_nacional "
                    + "FROM estudiante e "
                    + "JOIN prueba p   ON p.id_estudiante = e.id_estudiante "
                    + "JOIN registro rg ON rg.id_registro = p.id_registro "
                    + "JOIN resultados rdo ON rdo.id_registro = rg.id_registro "
                    + "WHERE 1=1 "
            );

            List<Object> params = new ArrayList<>();

            // Caso 1: estudiante logueado
            if (idUsuario != null) {
                sql.append(" AND e.id_usuario = ? ");
                params.add(idUsuario);
            }

            // Caso 2: selección desde InterPersonales (cc y/o número de registro)
            if (cc != null && !cc.trim().isEmpty()) {
                sql.append(" AND e.cc = ? ");
                params.add(cc.trim());
            }

            if (numeroRegistro != null && !numeroRegistro.trim().isEmpty()) {
                // numeroRegistro viene de la vista como "EK2023..." -> se guarda en registro.codigo
                sql.append(" AND rg.codigo = ? ");
                params.add(numeroRegistro.trim());
            }

            // Si hay varias presentaciones del mismo estudiante, tomamos la más reciente
            sql.append(" ORDER BY p.ano DESC, p.semestre DESC LIMIT 1 ");

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombre = rs.getString("nombre");
                        apellido = rs.getString("apellido");
                        puntaje = rs.getDouble("puntaje_global");
                        percentil = rs.getInt("percentil_nacional");
                    }
                }
            }

            // =======================
            // 2) PINTAR EN LA PANTALLA
            // =======================
            if (nombre != null || apellido != null) {
                txtEstudiante.setText(
                        (nombre != null ? nombre : "") + " "
                        + (apellido != null ? apellido : "")
                );
            } else {
                txtEstudiante.setText("Estudiante no encontrado");
            }

            if (puntaje != null) {
                txtGlobal.setText(String.format("%.1f", puntaje));
            } else {
                txtGlobal.setText("Sin datos");
            }

            if (percentil != null) {
                txtPercentil.setText(String.valueOf(percentil));
                if (panelPercentil != null) {
                    panelPercentil.setPercentil(percentil.doubleValue());
                }
            } else {
                txtPercentil.setText("Sin datos");
                if (panelPercentil != null) {
                    panelPercentil.setPercentil(0);   // o quizá dejarla en gris
                }
            }

        } catch (Exception ex) {
            // Aquí es donde antes sólo poníamos Error
            ex.printStackTrace(); // Para ver el detalle en la consola de NetBeans
            txtEstudiante.setText("Error");
            txtGlobal.setText("Error");
            txtPercentil.setText("Error");
        }
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
        txtGlobal = new javax.swing.JLabel();
        txtPercentil = new javax.swing.JLabel();
        jPanel = new javax.swing.JPanel();
        txtEstudiante = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(94, 122, 178));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("RESULTADOS PERSONALES");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, -1, -1));

        txtGlobal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtGlobal.setForeground(new java.awt.Color(255, 255, 255));
        txtGlobal.setText("...");
        add(txtGlobal, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, -1, -1));

        txtPercentil.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil.setForeground(new java.awt.Color(255, 255, 255));
        txtPercentil.setText("...");
        add(txtPercentil, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 190, -1, -1));

        jPanel.setBackground(new java.awt.Color(238, 235, 235));
        add(jPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, 520, 200));

        txtEstudiante.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtEstudiante.setForeground(new java.awt.Color(255, 255, 255));
        txtEstudiante.setText("...");
        add(txtEstudiante, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Percentil Nacional");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 160, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Puntaje Global");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 160, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Estudiante:");
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 90, -1));

        jPanel1.setBackground(new java.awt.Color(26, 100, 173));
        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 890, 60));

        jPanel2.setBackground(new java.awt.Color(63, 142, 221));
        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 890, 90));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel txtEstudiante;
    private javax.swing.JLabel txtGlobal;
    private javax.swing.JLabel txtPercentil;
    // End of variables declaration//GEN-END:variables
}
