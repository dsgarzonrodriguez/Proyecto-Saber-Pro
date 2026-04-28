/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Modelo.Conexion;
import com.mycompany.saberpro.Modelo.EstudianteSeleccionado;
import com.mycompany.saberpro.Modelo.Usuario;
import static com.mycompany.saberpro.Vista.frmMenu.jDesktopPane_menu;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

/**
 *
 * @author juanf
 */
public class InterPersonal extends javax.swing.JInternalFrame {

    private boolean tienePruebas = false;
    private Integer idUsuario;          // id del usuario logueado
    private String ccUsuario;           // cc del estudiante asociado al usuario
    // lista de pares (año, semestre) que existen para este estudiante
    private final List<int[]> pruebas = new ArrayList<>();

    public InterPersonal() {
        initComponents();
        this.setClosable(true);
        this.setIconifiable(true);
        this.setResizable(false);
        this.setSize(new Dimension(433, 328));
        this.setTitle("CONSULTAR RESULTADOS");

        if (Usuario.usuarioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay un usuario estudiante logueado.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            btnBuscar.setEnabled(false);
            btnLimpiar.setEnabled(false);
            return;
        }

        this.idUsuario = Usuario.usuarioActual.getId_usuario();
        this.ccUsuario = obtenerCcPorUsuario(this.idUsuario);
        if (this.ccUsuario == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró un estudiante asociado a tu usuario.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            btnBuscar.setEnabled(false);
            btnLimpiar.setEnabled(false);
            return;
        }

        // 👇 AQUÍ guardamos si realmente hay pruebas
        this.tienePruebas = cargarPruebas();

        if (!this.tienePruebas) {
            // ya se mostró el mensaje dentro de cargarPruebas()
            return;
        }

        boxAno.addActionListener(e -> actualizarSemestres());
    }

    public boolean isTienePruebas() {
        return tienePruebas;
    }

    private String obtenerCcPorUsuario(int idUsuario) {
        String cc = null;

        String sql = "SELECT cc "
                + "FROM estudiante "
                + "WHERE id_usuario = ? "
                + "LIMIT 1";

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cc = rs.getString("cc");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error consultando el estudiante del usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return cc;
    }

    /**
     * Carga la lista de (año, semestre) en los combos para la cc del usuario.
     */
    private boolean cargarPruebas() {

        pruebas.clear();

        DefaultComboBoxModel<String> modelAno = new DefaultComboBoxModel<>();
        modelAno.addElement("Seleccione");

        String sql = "SELECT DISTINCT ano, semestre "
                + "FROM vista_resultados_detalle "
                + "WHERE cc = ? "
                + "ORDER BY ano DESC, semestre DESC";

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ccUsuario);
            Set<Integer> anos = new LinkedHashSet<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ano = rs.getInt("ano");
                    int sem = rs.getInt("semestre");
                    pruebas.add(new int[]{ano, sem});
                    anos.add(ano);
                }
            }

            if (pruebas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron resultados registrados para tu usuario.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                btnBuscar.setEnabled(false);
                btnLimpiar.setEnabled(false);
                return false;  // <- IMPORTANTE
            } else {
                for (Integer a : anos) {
                    modelAno.addElement(String.valueOf(a));
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando pruebas del estudiante: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boxAno.setModel(modelAno);

        DefaultComboBoxModel<String> modelSem = new DefaultComboBoxModel<>();
        modelSem.addElement("Seleccione");
        boxSemestre.setModel(modelSem);

        return true;   // sí tiene pruebas
    }

    /**
     * Actualiza los semestres disponibles según el año seleccionado.
     */
    private void actualizarSemestres() {
        Object sel = boxAno.getSelectedItem();
        DefaultComboBoxModel<String> modelSem = new DefaultComboBoxModel<>();
        modelSem.addElement("Seleccione");

        if (sel == null || "Seleccione".equals(sel.toString())) {
            boxSemestre.setModel(modelSem);
            return;
        }

        int anoSel;
        try {
            anoSel = Integer.parseInt(sel.toString());
        } catch (NumberFormatException e) {
            boxSemestre.setModel(modelSem);
            return;
        }

        Set<Integer> semestres = new LinkedHashSet<>();
        for (int[] par : pruebas) {
            if (par[0] == anoSel) {
                semestres.add(par[1]);
            }
        }

        for (Integer s : semestres) {
            modelSem.addElement(String.valueOf(s));
        }
        boxSemestre.setModel(modelSem);
    }

    /**
     * Limpia los combos.
     */
    private void limpiar() {
        boxAno.setSelectedIndex(0);
        boxSemestre.setSelectedIndex(0);
    }

    /**
     * Busca la prueba seleccionada (año, semestre) para la cc del usuario y
     * abre la ventana InterResultadoPersonal.
     */
    private void buscar() {
        Integer ano = getIntFromCombo(boxAno);
        Integer semestre = getIntFromCombo(boxSemestre);

        if (ano == null || semestre == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona año y semestre de la prueba.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cc = null;
        String numeroRegistro = null;

        String sql
                = "SELECT cc, numero_registro "
                + "FROM vista_resultados_detalle "
                + "WHERE cc = ? AND ano = ? AND semestre = ? "
                + "LIMIT 1";

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ccUsuario);
            ps.setInt(2, ano);
            ps.setInt(3, semestre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cc = rs.getString("cc");
                    numeroRegistro = rs.getString("numero_registro");
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error consultando la prueba seleccionada: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cc == null || numeroRegistro == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron resultados para el año " + ano
                    + " y semestre " + semestre + ".",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Guardar selección global (para que InterResultadoPersonal la use)
        EstudianteSeleccionado.setSeleccion(cc, numeroRegistro, ano, semestre);

        // Abrir InterResultadoPersonal centrado en el DesktopPane
        InterResultadoPersonal frm = new InterResultadoPersonal();
        JDesktopPane desktop = getDesktopPane();
        if (desktop == null) {
            // fallback por si se abre desde el menú principal
            desktop = jDesktopPane_menu;
        }
        if (desktop != null) {
            desktop.removeAll();
            desktop.repaint();
            desktop.add(frm);

            Dimension desktopSize = desktop.getSize();
            Dimension frameSize = frm.getSize();
            frm.setLocation(
                    (desktopSize.width - frameSize.width) / 2,
                    (desktopSize.height - frameSize.height) / 2
            );
        }
        frm.setVisible(true);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convierte el valor seleccionado en un combo a Integer, o null si es
     * "Seleccione".
     */
    private Integer getIntFromCombo(javax.swing.JComboBox<String> combo) {
        Object o = combo.getSelectedItem();
        if (o == null) {
            return null;
        }
        String s = o.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("Seleccione")) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
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

        jLabel2 = new javax.swing.JLabel();
        boxAno = new javax.swing.JComboBox<>();
        boxSemestre = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        Semestre = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Año");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

        boxAno.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxAno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(boxAno, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, 220, -1));

        boxSemestre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        boxSemestre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(boxSemestre, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 220, -1));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Seleccione la prueba");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        Semestre.setBackground(new java.awt.Color(255, 255, 255));
        Semestre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Semestre.setForeground(new java.awt.Color(255, 255, 255));
        Semestre.setText("Semestre");
        getContentPane().add(Semestre, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, -1, -1));

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 153));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 160, -1));

        btnBuscar.setBackground(new java.awt.Color(0, 0, 153));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 160, -1));

        jLabel1.setBackground(new java.awt.Color(94, 122, 178));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 410));

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 290));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
// Si en el constructor se detectó que NO hay pruebas, no hacemos nada
        if (!tienePruebas) {
            return;
        }

        // Usa el método buscar() que ya valida año/semestre y consulta la BD
        buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Semestre;
    private javax.swing.JComboBox<String> boxAno;
    private javax.swing.JComboBox<String> boxSemestre;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
