/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanf
 */
public class Modulos extends javax.swing.JPanel {

    /**
     * Creates new form Modulos
     */
    private Integer idUsuario;
    private String cc;
    private String numeroRegistro;
    private Integer anio;
    private Integer semestre;

    public Modulos() {
        initComponents();
    }

    public Modulos(Integer idUsuario, String cc, String numeroRegistro,
            Integer anio, Integer semestre) {
        initComponents();
        this.idUsuario = idUsuario;       // lo guardo solo por si lo necesitas en UI
        this.cc = cc;
        this.numeroRegistro = numeroRegistro;
        this.anio = anio;
        this.semestre = semestre;

        cargarResultadosPorModulo();
    }

    private void limpiarLabels() {
        txtModulo1.setText("...");
        txtModulo2.setText("...");
        txtModulo3.setText("...");
        txtModulo4.setText("...");
        txtModulo5.setText("...");

        txtPercentil1.setText("...");
        txtPercentil2.setText("...");
        txtPercentil3.setText("...");
        txtPercentil4.setText("...");
        txtPercentil5.setText("...");
    }

    private void cargarResultadosPorModulo() {
        limpiarLabels();

        StringBuilder sql = new StringBuilder(
                "SELECT modulo, puntaje_modulo, percentil_nacional_modulo "
                + "FROM vista_resultados_modulo_detalle "
                + "WHERE 1=1 "
        );

        // ⚠️ OJO: ya NO filtramos por id_usuario, porque esa columna no existe en la vista.
        // Si tu vista tiene cc y quieres usarla, puedes descomentar esto:
        // if (cc != null && !cc.trim().isEmpty()) {
        //     sql.append(" AND cc = ? ");
        // }
        if (numeroRegistro != null && !numeroRegistro.trim().isEmpty()) {
            sql.append(" AND numero_registro = ? ");
        }
        if (anio != null) {
            sql.append(" AND ano = ? ");
        }
        if (semestre != null) {
            sql.append(" AND semestre = ? ");
        }

        sql.append(" ORDER BY modulo ");

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;

            // Si usas cc en la vista, puedes ponerlo aquí:
            // if (cc != null && !cc.trim().isEmpty()) {
            //     ps.setString(idx++, cc.trim());
            // }
            if (numeroRegistro != null && !numeroRegistro.trim().isEmpty()) {
                ps.setString(idx++, numeroRegistro.trim());
            }
            if (anio != null) {
                ps.setInt(idx++, anio);
            }
            if (semestre != null) {
                ps.setInt(idx++, semestre);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String modulo = rs.getString("modulo");
                    double puntaje = rs.getDouble("puntaje_modulo");
                    int percentil = rs.getInt("percentil_nacional_modulo");

                    if (modulo == null) {
                        continue;
                    }
                    String m = modulo.trim().toUpperCase();

                    if (m.contains("COMUNICACIÓN") || m.contains("COMUNICACION")) {
                        setFila(1, puntaje, percentil);
                    } else if (m.contains("LECTURA")) {
                        setFila(2, puntaje, percentil);
                    } else if (m.contains("RAZONAMIENTO")) {
                        setFila(3, puntaje, percentil);
                    } else if (m.contains("COMPETENCIAS CIUDADANAS")) {
                        setFila(4, puntaje, percentil);
                    } else if (m.contains("INGLÉS") || m.contains("INGLES")) {
                        setFila(5, puntaje, percentil);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("Error cargando resultados por módulo: " + ex.getMessage());

            txtModulo1.setText("Error");
            txtModulo2.setText("Error");
            txtModulo3.setText("Error");
            txtModulo4.setText("Error");
            txtModulo5.setText("Error");

            txtPercentil1.setText("Error");
            txtPercentil2.setText("Error");
            txtPercentil3.setText("Error");
            txtPercentil4.setText("Error");
            txtPercentil5.setText("Error");
        }
    }

    private void setFila(int fila, double puntaje, int percentil) {
        String puntajeStr = String.format("%.2f", puntaje);
        String percentilStr = String.valueOf(percentil);

        switch (fila) {
            case 1:
                txtModulo1.setText(puntajeStr);
                txtPercentil1.setText(percentilStr);
                break;
            case 2:
                txtModulo2.setText(puntajeStr);
                txtPercentil2.setText(percentilStr);
                break;
            case 3:
                txtModulo3.setText(puntajeStr);
                txtPercentil3.setText(percentilStr);
                break;
            case 4:
                txtModulo4.setText(puntajeStr);
                txtPercentil4.setText(percentilStr);
                break;
            case 5:
                txtModulo5.setText(puntajeStr);
                txtPercentil5.setText(percentilStr);
                break;
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtModulo1 = new javax.swing.JLabel();
        txtPercentil1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtModulo2 = new javax.swing.JLabel();
        txtPercentil2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtModulo3 = new javax.swing.JLabel();
        txtPercentil3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtModulo4 = new javax.swing.JLabel();
        txtPercentil4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtModulo5 = new javax.swing.JLabel();
        txtPercentil5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(63, 142, 221));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Comunicación escrita");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Lectura crítica");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Razonamiento cuantitativo");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Competencias ciudadanas");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Inglés");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 51, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(47, 47, 47))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, 310, 200));

        txtModulo1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtModulo1.setText("...");

        txtPercentil1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil1.setText("...");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(txtModulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                .addComponent(txtPercentil1)
                .addGap(94, 94, 94))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModulo1)
                    .addComponent(txtPercentil1))
                .addContainerGap())
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 220, 440, 40));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        txtModulo2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtModulo2.setText("...");

        txtPercentil2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil2.setText("...");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(txtModulo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(txtPercentil2)
                .addGap(94, 94, 94))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModulo2)
                    .addComponent(txtPercentil2))
                .addContainerGap())
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 260, 440, 40));

        txtModulo3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtModulo3.setText("...");

        txtPercentil3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil3.setText("...");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(txtModulo3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(txtPercentil3)
                .addGap(93, 93, 93))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModulo3)
                    .addComponent(txtPercentil3))
                .addContainerGap())
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 300, 440, 40));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        txtModulo4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtModulo4.setText("...");

        txtPercentil4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil4.setText("...");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(txtModulo4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 217, Short.MAX_VALUE)
                .addComponent(txtPercentil4)
                .addGap(93, 93, 93))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModulo4)
                    .addComponent(txtPercentil4))
                .addContainerGap())
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 340, 440, 40));

        txtModulo5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtModulo5.setText("...");

        txtPercentil5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPercentil5.setText("...");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(txtModulo5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(txtPercentil5)
                .addGap(92, 92, 92))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModulo5)
                    .addComponent(txtPercentil5))
                .addContainerGap())
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 380, 440, 40));

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setText("Puntaje Módulo");
        jPanel8.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Percentil Nacional");
        jPanel8.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 20, -1, -1));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 750, 60));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("RESULTADOS POR MÓDULO");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1050, 620));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel txtModulo1;
    private javax.swing.JLabel txtModulo2;
    private javax.swing.JLabel txtModulo3;
    private javax.swing.JLabel txtModulo4;
    private javax.swing.JLabel txtModulo5;
    private javax.swing.JLabel txtPercentil1;
    private javax.swing.JLabel txtPercentil2;
    private javax.swing.JLabel txtPercentil3;
    private javax.swing.JLabel txtPercentil4;
    private javax.swing.JLabel txtPercentil5;
    // End of variables declaration//GEN-END:variables
}
