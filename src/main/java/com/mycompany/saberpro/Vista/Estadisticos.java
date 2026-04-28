/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author juanf
 */
public class Estadisticos extends javax.swing.JPanel {

    /**
     * Creates new form Estadisticos
     */
    private Integer anio;
    private Integer semestre;

    public Estadisticos() {
        initComponents();
    }
    // El que usamos de verdad

    public Estadisticos(Integer anio, Integer semestre) {
        initComponents();
        this.anio = anio;
        this.semestre = semestre;

        // Mostrar el periodo en los labels
        if (anio != null) {
            btnAno.setText(String.valueOf(anio));
        } else {
            btnAno.setText("Todos");
        }

        if (semestre != null) {
            btnSemestre.setText(String.valueOf(semestre));
        } else {
            btnSemestre.setText("Todos");
        }

        cargarEstadisticos();
    }

    private void cargarEstadisticos() {
        if (anio == null || semestre == null) {
            // sin año/semestre no podemos calcular nada
            return;
        }

        cargarGlobal();
        cargarPorModulo();
    }

    // =========================
    //   BLOQUE GLOBAL ARRIBA
    // =========================
    private void cargarGlobal() {
        String sql
                = "SELECT COUNT(*) AS n, "
                + "       COALESCE(AVG(puntaje_global),0) AS media, "
                + "       COALESCE(VAR_SAMP(puntaje_global),0) AS varianza, "
                + "       COALESCE(STDDEV_SAMP(puntaje_global),0) AS sd "
                + "FROM vista_resultados_detalle "
                + "WHERE ano = ? AND semestre = ? "
                + "  AND puntaje_global IS NOT NULL "
                + "  AND puntaje_global > 0";

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, semestre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long n = rs.getLong("n");
                    double media = rs.getDouble("media");
                    double var = rs.getDouble("varianza");
                    double sd = rs.getDouble("sd");
                    double cv = (media != 0) ? (sd / media) * 100.0 : 0.0;

                    // AJUSTA estos nombres a tus labels:
                    txtTotal.setText(String.valueOf(n));
                    txtPromedio.setText(String.format("%.2f", media));
                    txtVarianza.setText(String.format("%.2f", var));
                    txtCoeficiente.setText(String.format("%.2f", cv));
                }
            }

        } catch (Exception ex) {
            System.err.println("Error cargando indicadores globales: " + ex.getMessage());
        }
    }

    // =========================
    //   TABLA POR MÓDULO
    // =========================
    private void cargarPorModulo() {
        String sql
                = "SELECT modulo, "
                + "       COUNT(*) AS n, "
                + "       COALESCE(AVG(puntaje_modulo),0) AS media, "
                + "       COALESCE(VAR_SAMP(puntaje_modulo),0) AS varianza, "
                + "       COALESCE(STDDEV_SAMP(puntaje_modulo),0) AS sd "
                + "FROM vista_resultados_modulo_detalle "
                + "WHERE ano = ? AND semestre = ? "
                + "  AND puntaje_modulo IS NOT NULL "
                + "  AND puntaje_modulo > 0 "
                + "GROUP BY modulo";

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, semestre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String modulo = rs.getString("modulo");
                    long n = rs.getLong("n");
                    double media = rs.getDouble("media");
                    double var = rs.getDouble("varianza");
                    double sd = rs.getDouble("sd");
                    double cv = (media != 0) ? (sd / media) * 100.0 : 0.0;

                    if (modulo == null) {
                        continue;
                    }
                    String m = modulo.trim().toUpperCase();

                    // Mapea a la fila correcta según el nombre
                    // (Ajusta los contains si tu texto es distinto)
                    if (m.contains("COMUNICACIÓN") || m.contains("COMUNICACION")) {
                        setFilaModulo(txtPromedio1, txtVarianza1, txtDispersion1, txtTotal1,
                                media, var, cv, n);
                    } else if (m.contains("RAZONAMIENTO")) {
                        setFilaModulo(txtPromedio2, txtVarianza2, txtDispersion2, txtTotal2,
                                media, var, cv, n);
                    } else if (m.contains("LECTURA")) {
                        setFilaModulo(txtPromedio3, txtVarianza3, txtDispersion3, txtTotal3,
                                media, var, cv, n);
                    } else if (m.contains("COMPETENCIAS CIUDADANAS")) {
                        setFilaModulo(txtPromedio4, txtVarianza4, txtDispersion4, txtTotal4,
                                media, var, cv, n);
                    } else if (m.contains("INGLÉS") || m.contains("INGLES")) {
                        setFilaModulo(txtPromedio5, txtVarianza5, txtDispersion5, txtTotal5,
                                media, var, cv, n);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("Error cargando indicadores por módulo: " + ex.getMessage());
        }
    }

    private void setFilaModulo(javax.swing.JLabel labProm,
            javax.swing.JLabel labVar,
            javax.swing.JLabel labDisp,
            javax.swing.JLabel labTotal,
            double media, double var, double cv, long n) {

        labProm.setText(String.format("%.2f", media));
        labVar.setText(String.format("%.2f", var));
        labDisp.setText(String.format("%.2f", cv));
        labTotal.setText(String.valueOf(n));
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
        txtDispersion5 = new javax.swing.JLabel();
        txtVarianza3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtPromedio2 = new javax.swing.JLabel();
        txtVarianza1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtPromedio1 = new javax.swing.JLabel();
        txtDispersion1 = new javax.swing.JLabel();
        txtTotal4 = new javax.swing.JLabel();
        txtPromedio3 = new javax.swing.JLabel();
        txtPromedio5 = new javax.swing.JLabel();
        txtTotal1 = new javax.swing.JLabel();
        txtDispersion2 = new javax.swing.JLabel();
        txtTotal5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtDispersion3 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtTotal3 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtTotal2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtVarianza4 = new javax.swing.JLabel();
        txtVarianza2 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtVarianza5 = new javax.swing.JLabel();
        txtPromedio4 = new javax.swing.JLabel();
        txtDispersion4 = new javax.swing.JLabel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel22 = new javax.swing.JLabel();
        txtVarianza = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtPromedio = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel8 = new javax.swing.JLabel();
        txtCoeficiente = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtTotal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        btnAno = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnSemestre = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(94, 122, 178));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtDispersion5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion5.setText("0");
        jPanel1.add(txtDispersion5, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 260, -1, -1));

        txtVarianza3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza3.setText("0");
        jPanel1.add(txtVarianza3, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 180, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("INGLÉS");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        txtPromedio2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio2.setText("0");
        jPanel1.add(txtPromedio2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 140, -1, -1));

        txtVarianza1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza1.setText("0");
        jPanel1.add(txtVarianza1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, -1, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("DISPERSIÓN");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 50, -1, -1));

        txtPromedio1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio1.setText("0");
        jPanel1.add(txtPromedio1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 100, -1, -1));

        txtDispersion1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion1.setText("0");
        jPanel1.add(txtDispersion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 100, -1, -1));

        txtTotal4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal4.setText("0");
        jPanel1.add(txtTotal4, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 220, -1, -1));

        txtPromedio3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio3.setText("0");
        jPanel1.add(txtPromedio3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 180, -1, -1));

        txtPromedio5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio5.setText("0");
        jPanel1.add(txtPromedio5, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 260, -1, -1));

        txtTotal1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal1.setText("0");
        jPanel1.add(txtTotal1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 100, -1, -1));

        txtDispersion2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion2.setText("0");
        jPanel1.add(txtDispersion2, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 140, -1, -1));

        txtTotal5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal5.setText("0");
        jPanel1.add(txtTotal5, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 260, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("LECTURA CRÍTICA");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("PROMEDIO");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, -1, -1));

        txtDispersion3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion3.setText("0");
        jPanel1.add(txtDispersion3, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 180, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setText("VARIANZA");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 50, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("RAZONAMIENTO CUANTITATIVO");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("COMUNICACIÓN ESCRITA");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        txtTotal3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal3.setText("0");
        jPanel1.add(txtTotal3, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 180, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setText("COMPETENCIAS GENÉRICAS ");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        txtTotal2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal2.setText("0");
        jPanel1.add(txtTotal2, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 140, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("TOTAL");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 50, -1, -1));

        txtVarianza4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza4.setText("0");
        jPanel1.add(txtVarianza4, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 220, -1, -1));

        txtVarianza2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza2.setText("0");
        jPanel1.add(txtVarianza2, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 140, -1, -1));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("COMPETENCIAS CIUDADANAS");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        txtVarianza5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza5.setText("0");
        jPanel1.add(txtVarianza5, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 260, -1, -1));

        txtPromedio4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio4.setText("0");
        jPanel1.add(txtPromedio4, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 220, -1, -1));

        txtDispersion4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion4.setText("0");
        jPanel1.add(txtDispersion4, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 220, -1, -1));

        filler12.setBackground(new java.awt.Color(255, 255, 255));
        filler12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler12, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 360, 270));

        filler2.setBackground(new java.awt.Color(255, 255, 255));
        filler2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 930, 80));

        filler4.setBackground(new java.awt.Color(255, 255, 255));
        filler4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 930, 160));

        filler1.setBackground(new java.awt.Color(255, 255, 255));
        filler1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 930, 40));

        filler9.setBackground(new java.awt.Color(255, 255, 255));
        filler9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 120, 270));

        filler5.setBackground(new java.awt.Color(255, 255, 255));
        filler5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 850, 200));

        filler3.setBackground(new java.awt.Color(255, 255, 255));
        filler3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 930, 120));

        filler11.setBackground(new java.awt.Color(255, 255, 255));
        filler11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler11, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 240, 270));

        filler10.setBackground(new java.awt.Color(255, 255, 255));
        filler10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 930, 270));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, -1));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, 980, 330));

        jPanel3.setBackground(new java.awt.Color(94, 122, 178));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        filler14.setBackground(new java.awt.Color(255, 255, 255));
        filler14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.add(filler14, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 110, 160));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setText("VARIANZA");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, -1, -1));

        txtVarianza.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza.setText("0");
        jPanel3.add(txtVarianza, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, -1, -1));

        filler7.setBackground(new java.awt.Color(255, 255, 255));
        filler7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.add(filler7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 370, 80));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("TOTAL DE DATOS");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, -1, -1));

        filler13.setBackground(new java.awt.Color(255, 255, 255));
        filler13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.add(filler13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 370, 160));

        txtPromedio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio.setText("0");
        jPanel3.add(txtPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, -1, -1));

        filler8.setBackground(new java.awt.Color(255, 255, 255));
        filler8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.add(filler8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 370, 120));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("PROMEDIO");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        txtCoeficiente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtCoeficiente.setText("0");
        jPanel3.add(txtCoeficiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 190, -1, -1));

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("INDICADORES ESTADÍSTICOS");
        jPanel10.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 230, -1));

        jPanel3.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 370, 40));

        filler6.setBackground(new java.awt.Color(255, 255, 255));
        filler6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.add(filler6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 370, 40));

        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal.setText("0");
        jPanel3.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, -1, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("COEFICIENTE DE DISPERSIÓN");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 370, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 370, -1));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 370, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 370, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 370, -1));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 370, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 400, 220));

        jPanel9.setBackground(new java.awt.Color(94, 122, 178));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("AÑO");
        jPanel19.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 40, -1));

        btnAno.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAno.setText("...");
        jPanel19.add(btnAno, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 70, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("SEMESTRE");
        jPanel19.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 90, -1));

        btnSemestre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSemestre.setText("...");
        jPanel19.add(btnSemestre, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 20, -1));

        jPanel15.setBackground(new java.awt.Color(204, 204, 204));
        jPanel19.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 40));

        jPanel9.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 80));

        add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 50, -1, 150));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnAno;
    private javax.swing.JLabel btnSemestre;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel txtCoeficiente;
    private javax.swing.JLabel txtDispersion1;
    private javax.swing.JLabel txtDispersion2;
    private javax.swing.JLabel txtDispersion3;
    private javax.swing.JLabel txtDispersion4;
    private javax.swing.JLabel txtDispersion5;
    private javax.swing.JLabel txtPromedio;
    private javax.swing.JLabel txtPromedio1;
    private javax.swing.JLabel txtPromedio2;
    private javax.swing.JLabel txtPromedio3;
    private javax.swing.JLabel txtPromedio4;
    private javax.swing.JLabel txtPromedio5;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JLabel txtTotal1;
    private javax.swing.JLabel txtTotal2;
    private javax.swing.JLabel txtTotal3;
    private javax.swing.JLabel txtTotal4;
    private javax.swing.JLabel txtTotal5;
    private javax.swing.JLabel txtVarianza;
    private javax.swing.JLabel txtVarianza1;
    private javax.swing.JLabel txtVarianza2;
    private javax.swing.JLabel txtVarianza3;
    private javax.swing.JLabel txtVarianza4;
    private javax.swing.JLabel txtVarianza5;
    // End of variables declaration//GEN-END:variables
}
