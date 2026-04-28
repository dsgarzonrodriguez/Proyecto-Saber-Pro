/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.saberpro.Modelo.Conexion;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author juanf
 */
public class InterGenerarInforme extends javax.swing.JInternalFrame {

    /**
     * Creates new form InterGenerarInforme
     */
    public InterGenerarInforme() {
        initComponents();
        this.setSize(new Dimension(1001, 755));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("INFORME CON IA - SABER PRO");

        cargarCombos();

        jLabel.setVisible(false);
        jCarga.setVisible(false);

        // Barra en modo indeterminado (la típica "que se mueve")
        jCarga.setIndeterminate(true);
    }

    public javax.swing.JLabel getLabel() {
        return jLabel;
    }

    public javax.swing.JProgressBar getCarga() {
        return jCarga;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnGenerar() {
        return btnGenerar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JTextArea getTxtInforme() {
        return txtInforme;
    }

    // ==== Lectura de filtros ====
    private String getTextoSeleccionado(JComboBox<String> combo) {
        Object o = combo.getSelectedItem();
        if (o == null) {
            return null;
        }
        String s = o.toString().trim();
        return (s.isEmpty() || s.equalsIgnoreCase("Todos")) ? null : s;
    }

    public Integer getAnio() {
        return parseIntFromCombo(boxAno);
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

    private Integer parseIntFromCombo(JComboBox<?> combo) {
        if (combo == null) {
            return null;
        }
        Object o = combo.getSelectedItem();
        if (o == null) {
            return null;
        }
        String s = o.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("Todos")) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void limpiarFiltrosYTexto() {
        boxAno.setSelectedIndex(0);
        boxSemestre.setSelectedIndex(0);
        boxPrograma.setSelectedIndex(0);
        boxCiudad.setSelectedIndex(0);
        txtInforme.setText("");
    }

    // ========= Cargar combos desde la BD =========
    private void cargarCombos() {
        setComboValores(boxAno,
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
            JOptionPane.showMessageDialog(this,
                    "Error cargando combo: " + ex.getMessage());
        }
        combo.setModel(model);
        combo.setSelectedIndex(0);
    }

    /**
     * Este método lo llama el controlador cuando pulsas "Guardar PDF". Toma el
     * texto de la vista previa y lo exporta a un PDF sencillo.
     */
    public void guardarInformeComoPDF() {
        String texto = txtInforme.getText();
        if (texto == null || texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay informe para guardar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar informe como PDF");
        chooser.setSelectedFile(new File("informe-saberpro.pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // usuario canceló
        }

        File file = chooser.getSelectedFile();
        // Asegurar extensión .pdf
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            // Fuentes
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontTexto = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

            String[] lineas = texto.split("\\r?\\n");

            boolean tituloAgregado = false;

            for (String linea : lineas) {
                if (!tituloAgregado) {
                    // Primera línea como título centrado
                    Paragraph titulo = new Paragraph(linea, fontTitulo);
                    titulo.setAlignment(Element.ALIGN_CENTER);
                    titulo.setSpacingAfter(15f);
                    document.add(titulo);
                    tituloAgregado = true;
                } else {
                    Paragraph p = new Paragraph(linea, fontTexto);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    p.setSpacingAfter(4f);
                    document.add(p);
                }
            }

            document.close();

            JOptionPane.showMessageDialog(this,
                    "PDF guardado en:\n" + file.getAbsolutePath(),
                    "PDF generado",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (DocumentException | IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar PDF: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        jLabel1 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        boxCiudad = new javax.swing.JComboBox<>();
        jLabel = new javax.swing.JLabel();
        boxAno = new javax.swing.JComboBox<>();
        boxSemestre = new javax.swing.JComboBox<>();
        boxPrograma = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtInforme = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        btnGenerar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jCarga = new javax.swing.JProgressBar();

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("CIUDAD");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, -1, -1));

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 153));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 140, 130, 30));

        boxCiudad.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxCiudad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(boxCiudad, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 60, -1, -1));

        jLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel.setForeground(new java.awt.Color(255, 255, 255));
        jLabel.setText("Cargando...");
        jPanel1.add(jLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        boxAno.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxAno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(boxAno, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        boxSemestre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxSemestre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(boxSemestre, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, -1, -1));

        boxPrograma.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxPrograma.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(boxPrograma, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("AÑO");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("VISTA PREVIA");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 280, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("PROGRAMA");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, -1, -1));

        btnGuardar.setBackground(new java.awt.Color(0, 0, 153));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 270, 130, 30));

        txtInforme.setColumns(20);
        txtInforme.setRows(5);
        jScrollPane1.setViewportView(txtInforme);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 910, 380));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("SEMESTRE");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        btnGenerar.setBackground(new java.awt.Color(0, 0, 153));
        btnGenerar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGenerar.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerar.setText("Generar");
        jPanel1.add(btnGenerar, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, 130, 30));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("GENERAR INFORME CON IA:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, -1));
        jPanel1.add(jCarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 989, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxAno;
    private javax.swing.JComboBox<String> boxCiudad;
    private javax.swing.JComboBox<String> boxPrograma;
    private javax.swing.JComboBox<String> boxSemestre;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JProgressBar jCarga;
    private javax.swing.JLabel jLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtInforme;
    // End of variables declaration//GEN-END:variables
}
