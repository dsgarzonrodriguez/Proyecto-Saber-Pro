/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Controlador.CtrlPersonales;
import com.mycompany.saberpro.Modelo.Conexion;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author juanf
 */
public class InterResultadosGenerales extends javax.swing.JInternalFrame {

    /**
     * Creates new form InterResultadosGenerales
     */
    public InterResultadosGenerales() {
        initComponents();          // NetBeans ya agregó todos tus controles
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("RESULTADOS GENERALES");

        jScrollPane3.getVerticalScrollBar().setUnitIncrement(40);

        btnExcel.setOpaque(false);
        btnExcel.setContentAreaFilled(false);
        btnExcel.setBorderPainted(true);
        btnExcel.setForeground(Color.WHITE);
        btnExcel.setFocusPainted(false);

        btnPDF.setOpaque(false);
        btnPDF.setContentAreaFilled(false);
        btnPDF.setBorderPainted(true);
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFocusPainted(false);

        // (opcional) que uno venga marcado
        btnExcel.setSelected(true);

        // KPIs en blanco
        txtTotal.setText("0");
        txtPromedio.setText("0,0");
        txtVarianza.setText("0,00");
        txtCoeficiente.setText("0,0 %");

        panel1.setLayout(new java.awt.BorderLayout());
        panel3.setLayout(new java.awt.BorderLayout());

        // Agrupar radios (por si luego usas exportar)
        ButtonGroup grp = new ButtonGroup();
        grp.add(btnExcel);
        grp.add(btnPDF);

        // Cargar combos (NO enganchar botones aquí)
        cargarCombos();
    }

    public JButton getBtnExportar() {
        return btnExportar;
    }

    public javax.swing.JPanel getPanel1() {
        return panel1;
    }


    public javax.swing.JPanel getPanel3() {
        return panel3;
    }

    public String getFormatoSeleccionado() {
        if (btnPDF.isSelected()) {
            return "PDF";
        }
        if (btnExcel.isSelected()) {
            return "Excel";
        }
        return null; // ninguno seleccionado
    }

    public javax.swing.JRadioButton getOptExcel() {
        return btnExcel;
    }

    public javax.swing.JRadioButton getOptPDF() {
        return btnPDF;
    }

    private String getTextoSeleccionado(JComboBox<String> combo) {
        Object o = combo.getSelectedItem();
        if (o == null) {
            return null;
        }
        String s = o.toString().trim();
        return (s.isEmpty() || s.equalsIgnoreCase("Todos")) ? null : s;
    }

    public Integer getAnio() {
        return parseIntFromCombo(boxAnio);
    }

    public Integer getSemestre() {
        return parseIntFromCombo(boxSemestre);
    }

    public String getProgramaTexto() {
        return getTextoSeleccionado(boxPrograma);
    }

    public String getCiudadTexto() {
        return getTextoSeleccionado(boxCiudad);
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JTable getTabla() {
        return jTable;
    }

    // ========== Filtros numéricos ==========
    private Integer parseIntFromCombo(JComboBox<?> combo) {
        if (combo == null) {
            return null;
        }
        Object o = combo.getSelectedItem();
        if (o == null) {
            return null;
        }

        String s = o.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("Todos") || s.startsWith("Item")) {
            return null;
        }

        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    // ======== KPIs globales (labels de la derecha) ========
    public void setTotal(long n) {
        txtTotal.setText(String.valueOf(n));
    }

    public void setPromedio(String s) {
        txtPromedio.setText(s);
    }

    public void setVarianza(String s) {
        txtVarianza.setText(s);
    }

    public void setCv(String s) {
        txtCoeficiente.setText(s);
    }

    // ======== KPIs por módulo (tabla blanca de abajo) ========
    public void limpiarIndicadoresModulos() {
        // Fila 1
        txtPromedio1.setText("");
        txtVarianza1.setText("");
        txtDispersion1.setText("");
        txtTotal1.setText("");

        // Fila 2
        txtPromedio2.setText("");
        txtVarianza2.setText("");
        txtDispersion2.setText("");
        txtTotal2.setText("");

        // Fila 3
        txtPromedio3.setText("");
        txtVarianza3.setText("");
        txtDispersion3.setText("");
        txtTotal3.setText("");

        // Fila 4
        txtPromedio4.setText("");
        txtVarianza4.setText("");
        txtDispersion4.setText("");
        txtTotal4.setText("");

        // Fila 5
        txtPromedio5.setText("");
        txtVarianza5.setText("");
        txtDispersion5.setText("");
        txtTotal5.setText("");
    }

    /**
     * fila: 0..4 => 0: COMUNICACIÓN ESCRITA, 1: RAZONAMIENTO..., etc.
     */
    public void setIndicadoresModulo(int fila, String promedio, String varianza, String dispersion) {
        switch (fila) {
            case 0 -> {
                txtPromedio1.setText(promedio);
                txtVarianza1.setText(varianza);
                txtDispersion1.setText(dispersion);
            }
            case 1 -> {
                txtPromedio2.setText(promedio);
                txtVarianza2.setText(varianza);
                txtDispersion2.setText(dispersion);
            }
            case 2 -> {
                txtPromedio3.setText(promedio);
                txtVarianza3.setText(varianza);
                txtDispersion3.setText(dispersion);
            }
            case 3 -> {
                txtPromedio4.setText(promedio);
                txtVarianza4.setText(varianza);
                txtDispersion4.setText(dispersion);
            }
            case 4 -> {
                txtPromedio5.setText(promedio);
                txtVarianza5.setText(varianza);
                txtDispersion5.setText(dispersion);
            }
        }
    }

    /**
     * fila: 0..4, texto típico: "(n = 35)"
     */
    public void setNModulo(int fila, String texto) {
        switch (fila) {
            case 0 ->
                txtTotal1.setText(texto);
            case 1 ->
                txtTotal2.setText(texto);
            case 2 ->
                txtTotal3.setText(texto);
            case 3 ->
                txtTotal4.setText(texto);
            case 4 ->
                txtTotal5.setText(texto);
        }
    }

    public void limpiarFiltros() {
        boxAnio.setSelectedIndex(0);
        boxSemestre.setSelectedIndex(0);
        boxPrograma.setSelectedIndex(0);
        boxCiudad.setSelectedIndex(0);

        limpiarIndicadoresModulos();
    }

    // ========= Cargar combos desde la vista SQL =========
    private void cargarCombos() {
        setComboValores(boxAnio,
                "SELECT DISTINCT ano FROM vista_resultados_detalle ORDER BY 1 DESC");

        setComboValores(boxSemestre,
                "SELECT DISTINCT semestre FROM vista_resultados_detalle ORDER BY 1 DESC");

        setComboValores(boxPrograma,
                "SELECT DISTINCT programa FROM vista_resultados_detalle ORDER BY 1");

        setComboValores(boxCiudad,
                "SELECT DISTINCT ciudad FROM vista_resultados_detalle ORDER BY 1");
    }

    private void setComboValores(JComboBox<String> combo, String sql) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Todos");
        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addElement(rs.getString(1));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando combo: " + ex.getMessage());
        }
        combo.setModel(model);
        combo.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        btnBuscar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        panel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        panel3 = new javax.swing.JPanel();
        boxCiudad = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        boxPrograma = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        boxAnio = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        boxSemestre = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btnExportar = new javax.swing.JButton();
        btnExcel = new javax.swing.JRadioButton();
        btnPDF = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnConsultar = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtTotal3 = new javax.swing.JLabel();
        txtPromedio5 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel25 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtVarianza2 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtTotal2 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtPromedio3 = new javax.swing.JLabel();
        txtPromedio2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtVarianza1 = new javax.swing.JLabel();
        txtDispersion4 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtPromedio4 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVarianza5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtTotal5 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtTotal4 = new javax.swing.JLabel();
        txtDispersion3 = new javax.swing.JLabel();
        txtPromedio1 = new javax.swing.JLabel();
        txtDispersion1 = new javax.swing.JLabel();
        txtTotal1 = new javax.swing.JLabel();
        txtDispersion2 = new javax.swing.JLabel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtDispersion5 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtVarianza3 = new javax.swing.JLabel();
        txtVarianza4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        txtCoeficiente = new javax.swing.JLabel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtPromedio = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        txtVarianza = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(94, 122, 178));
        jPanel4.setPreferredSize(new java.awt.Dimension(1600, 1580));

        btnBuscar.setBackground(new java.awt.Color(0, 0, 153));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar");

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 153));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Año", "Semestre", "Nombre", "Apellido", "CC", "Número de registro", "Programa", "Puntaje global", "Percentil nacielal global", "Ciudad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable);

        panel1.setLayout(new java.awt.BorderLayout());

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Grafica 1");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Grafica 2");

        panel3.setLayout(new java.awt.BorderLayout());

        boxCiudad.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxCiudad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Ciudad");

        boxPrograma.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxPrograma.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Programa");

        boxAnio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Año");

        boxSemestre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxSemestre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Semestre");

        btnExportar.setBackground(new java.awt.Color(0, 0, 153));
        btnExportar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnExportar.setForeground(new java.awt.Color(255, 255, 255));
        btnExportar.setText("Exportar");

        btnExcel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnExcel.setText("Excel");

        btnPDF.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnPDF.setText("PDF");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Exportacion del reporte de las consultas, escoja el formato.");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("CONSULTAR POR ESTUDIANTE");

        btnConsultar.setBackground(new java.awt.Color(0, 0, 153));
        btnConsultar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnConsultar.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Todo dato de COEFICIENTE DE DISPERSIÓN");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("mayor al 30% no es valido.");

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotal3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal3.setText("0");
        jPanel1.add(txtTotal3, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 180, -1, -1));

        txtPromedio5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio5.setText("0");
        jPanel1.add(txtPromedio5, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, -1, -1));

        filler5.setBackground(new java.awt.Color(255, 255, 255));
        filler5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 850, 200));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setText("VARIANZA");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 50, -1, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("PROMEDIO");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 50, -1, -1));

        filler10.setBackground(new java.awt.Color(255, 255, 255));
        filler10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 930, 270));

        txtVarianza2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza2.setText("0");
        jPanel1.add(txtVarianza2, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 140, -1, -1));

        filler4.setBackground(new java.awt.Color(255, 255, 255));
        filler4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 930, 160));

        txtTotal2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal2.setText("0");
        jPanel1.add(txtTotal2, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 140, -1, -1));

        filler1.setBackground(new java.awt.Color(255, 255, 255));
        filler1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 930, 40));

        txtPromedio3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio3.setText("0");
        jPanel1.add(txtPromedio3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, -1, -1));

        txtPromedio2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio2.setText("0");
        jPanel1.add(txtPromedio2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 140, -1, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("COMUNICACIÓN ESCRITA");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("LECTURA CRÍTICA");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, -1, -1));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("COMPETENCIAS CIUDADANAS");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, -1, -1));

        txtVarianza1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza1.setText("0");
        jPanel1.add(txtVarianza1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 100, -1, -1));

        txtDispersion4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion4.setText("0");
        jPanel1.add(txtDispersion4, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 220, -1, -1));

        filler2.setBackground(new java.awt.Color(255, 255, 255));
        filler2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 930, 80));

        txtPromedio4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio4.setText("0");
        jPanel1.add(txtPromedio4, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, -1, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("DISPERSIÓN");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setText("COMPETENCIAS GENÉRICAS ");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("RAZONAMIENTO CUANTITATIVO");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("INGLÉS");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        txtVarianza5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza5.setText("0");
        jPanel1.add(txtVarianza5, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 260, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("TOTAL");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 50, -1, -1));

        txtTotal5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal5.setText("0");
        jPanel1.add(txtTotal5, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 260, -1, -1));

        filler3.setBackground(new java.awt.Color(255, 255, 255));
        filler3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 930, 120));

        txtTotal4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal4.setText("0");
        jPanel1.add(txtTotal4, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 220, -1, -1));

        txtDispersion3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion3.setText("0");
        jPanel1.add(txtDispersion3, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 180, -1, -1));

        txtPromedio1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio1.setText("0");
        jPanel1.add(txtPromedio1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 100, -1, -1));

        txtDispersion1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion1.setText("0");
        jPanel1.add(txtDispersion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 100, -1, -1));

        txtTotal1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal1.setText("0");
        jPanel1.add(txtTotal1, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 100, -1, -1));

        txtDispersion2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion2.setText("0");
        jPanel1.add(txtDispersion2, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 140, -1, -1));

        filler11.setBackground(new java.awt.Color(255, 255, 255));
        filler11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler11, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 240, 270));

        filler12.setBackground(new java.awt.Color(255, 255, 255));
        filler12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler12, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 360, 270));

        txtDispersion5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDispersion5.setText("0");
        jPanel1.add(txtDispersion5, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 260, -1, -1));

        filler9.setBackground(new java.awt.Color(255, 255, 255));
        filler9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(filler9, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 120, 270));

        txtVarianza3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza3.setText("0");
        jPanel1.add(txtVarianza3, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 180, -1, -1));

        txtVarianza4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza4.setText("0");
        jPanel1.add(txtVarianza4, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 220, -1, -1));

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

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 930, 70));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 930, 40));

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

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 930, 40));

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

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 930, 40));

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

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 930, 40));

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

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 930, 40));

        jPanel9.setBackground(new java.awt.Color(94, 122, 178));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setText("VARIANZA");
        jPanel9.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        txtCoeficiente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtCoeficiente.setText("0");
        jPanel9.add(txtCoeficiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 180, -1, -1));

        filler14.setBackground(new java.awt.Color(255, 255, 255));
        filler14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel9.add(filler14, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 110, 160));

        filler6.setBackground(new java.awt.Color(255, 255, 255));
        filler6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel9.add(filler6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 40));

        filler8.setBackground(new java.awt.Color(255, 255, 255));
        filler8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel9.add(filler8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 120));

        txtPromedio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPromedio.setText("0");
        jPanel9.add(txtPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, -1, -1));

        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotal.setText("0");
        jPanel9.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, -1));

        filler13.setBackground(new java.awt.Color(255, 255, 255));
        filler13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel9.add(filler13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 160));

        txtVarianza.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtVarianza.setText("0");
        jPanel9.add(txtVarianza, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 140, -1, -1));

        filler7.setBackground(new java.awt.Color(255, 255, 255));
        filler7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel9.add(filler7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 80));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("COEFICIENTE DE DISPERSIÓN");
        jPanel9.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("PROMEDIO");
        jPanel9.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("TOTAL DE DATOS");
        jPanel9.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("INDICADORES ESTADÍSTICOS");
        jPanel10.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 230, -1));

        jPanel9.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 370, 40));

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

        jPanel9.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 40));

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

        jPanel9.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 370, 40));

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

        jPanel9.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 370, 40));

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

        jPanel9.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 370, 40));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGap(29, 29, 29)
                                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(91, 91, 91)
                                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 920, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(boxAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(47, 47, 47)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(boxSemestre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(57, 57, 57)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(209, 209, 209)
                                        .addComponent(jLabel6))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(boxPrograma, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(60, 60, 60)
                                        .addComponent(boxCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(172, 172, 172)
                                .addComponent(jLabel11)
                                .addGap(454, 454, 454)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(200, 200, 200)
                                    .addComponent(btnPDF))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGap(128, 128, 128)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4))
                            .addComponent(jLabel6))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boxAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boxSemestre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boxPrograma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boxCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnExcel)
                            .addComponent(btnPDF))
                        .addGap(18, 18, 18)
                        .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(63, 63, 63)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(352, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel4);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1540, 900));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        InterPersonales ip = new InterPersonales();
        new CtrlPersonales(ip);  // nombre correcto del controlador

        // Obtener el DesktopPane que contiene este JInternalFrame
        javax.swing.JDesktopPane desktop = getDesktopPane();
        if (desktop != null) {
            desktop.add(ip);
            ip.setVisible(true);
            ip.toFront();
        }
    }//GEN-LAST:event_btnConsultarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxAnio;
    private javax.swing.JComboBox<String> boxCiudad;
    private javax.swing.JComboBox<String> boxPrograma;
    private javax.swing.JComboBox<String> boxSemestre;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JRadioButton btnExcel;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JRadioButton btnPDF;
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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel3;
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
