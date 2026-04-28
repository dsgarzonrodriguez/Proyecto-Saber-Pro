/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Controlador.CtrlCuenta;
import com.mycompany.saberpro.Controlador.CtrlGenerarInforme;
import com.mycompany.saberpro.Controlador.CtrlResultadosGenerales;
import com.mycompany.saberpro.Controlador.CtrlRoles;
import com.mycompany.saberpro.Controlador.CtrlUsuario;
import com.mycompany.saberpro.Modelo.ConsultasRoles;
import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Modelo.Roles;
import com.mycompany.saberpro.Modelo.Usuario;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author juanf
 */
public class frmMenu extends javax.swing.JFrame {

    public static JDesktopPane jDesktopPane_menu;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmMenu.class.getName());

    /**
     * Creates new form frmMenu
     */
    public frmMenu() {
        initComponents();

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setTitle("SABER PRO");

        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());

        // Crear un JDesktopPane personalizado que pinte el fondo azul y el texto
        jDesktopPane_menu = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(63, 142, 221)); // color de fondo
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.setFont(new Font("Tahoma", Font.BOLD, 80));
                FontMetrics fm = g.getFontMetrics();
                String texto = "SABER PRO";
                int x = (getWidth() - fm.stringWidth(texto)) / 2;
                int y = (getHeight() / 2) + fm.getAscent() / 2;
                g.drawString(texto, x, y);
            }
        };

        // Ocupa todo el JFrame
        getContentPane().add(jDesktopPane_menu, java.awt.BorderLayout.CENTER);

        this.revalidate();
        this.repaint();

    }

    public void configurarPermisos() {
        // Obtenemos el usuario logueado
        Usuario usuario = Usuario.usuarioActual;

        if (usuario == null || usuario.getRol() == null) {
            System.err.println("⚠ No hay usuario logueado o el rol es nulo");
            return;
        }

        String rol = usuario.getRol().getNombre().toLowerCase();

        // 🔹 Por defecto, deshabilitamos todo
        jitemGestionCuenta.setVisible(false);
        jitemGestionUsuario.setVisible(false);
        jitemCargarUsuarios.setVisible(false);
        jitemGestionRoles.setVisible(false);
        jitemResultadoPersonal.setVisible(false);
        jitemResultadoGeneral.setVisible(false);
        jitemCargarResultados.setVisible(false);
        jitemGestionInformes.setVisible(false);
        jitemCargarInformes.setVisible(false);
        jitemConsultarPlan.setVisible(false);
        jitemGenerarInforme.setVisible(false);
        jMenu5.setVisible(false);
        jMenu6.setVisible(false);
        jMenu3.setVisible(false);

        // 🔹 Ahora activamos según el rol
        switch (rol) {
            case "estudiante" -> {
                jitemGestionCuenta.setVisible(true);
                jitemResultadoPersonal.setVisible(true);
            }
            case "profesor" -> {
                jitemGestionCuenta.setVisible(true);
                jMenu5.setVisible(true);
                jitemConsultarPlan.setVisible(true);
            }
            case "administrador" -> {
                jitemGestionCuenta.setVisible(true);
                jitemGestionUsuario.setVisible(true);
                jitemGestionRoles.setVisible(true);
                jitemResultadoPersonal.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemCargarResultados.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jitemCargarUsuarios.setVisible(true);
                jitemGenerarInforme.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);

            }
            case "decano" -> {
                jitemGestionCuenta.setVisible(true);
                jitemGestionUsuario.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);
            }
            case "director de programa" -> {
                jitemGestionCuenta.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemCargarResultados.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);
            }
            case "coordinador saber pro" -> {
                jitemGestionCuenta.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemCargarResultados.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jitemGenerarInforme.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);
            }
            case "comite de programa" -> {
                jitemGestionCuenta.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemCargarResultados.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);
            }
            case "secretaria de acreditacion" -> {
                jitemGestionCuenta.setVisible(true);
                jitemGestionUsuario.setVisible(true);
                jitemResultadoGeneral.setVisible(true);
                jitemGestionInformes.setVisible(true);
                jitemCargarInformes.setVisible(true);
                jitemConsultarPlan.setVisible(true);
                jMenu5.setVisible(true);
                jMenu6.setVisible(true);
                jMenu3.setVisible(true);
            }
            default -> {
                jitemGestionCuenta.setVisible(true);
            }
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jitemGestionCuenta = new javax.swing.JMenuItem();
        jitemGestionUsuario = new javax.swing.JMenuItem();
        jitemGestionRoles = new javax.swing.JMenuItem();
        jitemCargarUsuarios = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jitemResultadoPersonal = new javax.swing.JMenuItem();
        jitemResultadoGeneral = new javax.swing.JMenuItem();
        jitemCargarResultados = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jitemGenerarInforme = new javax.swing.JMenuItem();
        jitemGestionInformes = new javax.swing.JMenuItem();
        jitemCargarInformes = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jitemGestionPlan = new javax.swing.JMenuItem();
        jitemConsultarPlan = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jitemGestionarEvaluacion = new javax.swing.JMenuItem();
        jitemConsultarEvaluacion = new javax.swing.JMenuItem();
        jitemCerrarSesion = new javax.swing.JMenu();
        jitemCerrar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuario.png"))); // NOI18N
        jMenu1.setText("Usuario");
        jMenu1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jMenu1.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemGestionCuenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionCuenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuario.png"))); // NOI18N
        jitemGestionCuenta.setText("Gestionar cuenta");
        jitemGestionCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemGestionCuentaActionPerformed(evt);
            }
        });
        jMenu1.add(jitemGestionCuenta);

        jitemGestionUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/historial1.png"))); // NOI18N
        jitemGestionUsuario.setText("Gestionar usuarios");
        jitemGestionUsuario.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemGestionUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemGestionUsuarioActionPerformed(evt);
            }
        });
        jMenu1.add(jitemGestionUsuario);

        jitemGestionRoles.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionRoles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/configuraciones.png"))); // NOI18N
        jitemGestionRoles.setText("Gestionar roles");
        jitemGestionRoles.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemGestionRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemGestionRolesActionPerformed(evt);
            }
        });
        jMenu1.add(jitemGestionRoles);

        jitemCargarUsuarios.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemCargarUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nuevo-cliente.png"))); // NOI18N
        jitemCargarUsuarios.setText("Carga de usuarios");
        jitemCargarUsuarios.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemCargarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemCargarUsuariosActionPerformed(evt);
            }
        });
        jMenu1.add(jitemCargarUsuarios);

        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/producto.png"))); // NOI18N
        jMenu2.setText("Resultados");
        jMenu2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jMenu2.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemResultadoPersonal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemResultadoPersonal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nuevo-producto.png"))); // NOI18N
        jitemResultadoPersonal.setText("Resultado personal");
        jitemResultadoPersonal.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemResultadoPersonal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemResultadoPersonalActionPerformed(evt);
            }
        });
        jMenu2.add(jitemResultadoPersonal);

        jitemResultadoGeneral.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemResultadoGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/producto.png"))); // NOI18N
        jitemResultadoGeneral.setText("Resultado general");
        jitemResultadoGeneral.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemResultadoGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemResultadoGeneralActionPerformed(evt);
            }
        });
        jMenu2.add(jitemResultadoGeneral);

        jitemCargarResultados.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemCargarResultados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nuevo.png"))); // NOI18N
        jitemCargarResultados.setText("Cargar Resultados");
        jitemCargarResultados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemCargarResultadosActionPerformed(evt);
            }
        });
        jMenu2.add(jitemCargarResultados);

        jMenuBar1.add(jMenu2);

        jMenu3.setBackground(new java.awt.Color(0, 255, 153));
        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cliente.png"))); // NOI18N
        jMenu3.setText("Informes");
        jMenu3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jMenu3.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemGenerarInforme.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGenerarInforme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/reporte1.png"))); // NOI18N
        jitemGenerarInforme.setText("Generar Informe con IA");
        jitemGenerarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemGenerarInformeActionPerformed(evt);
            }
        });
        jMenu3.add(jitemGenerarInforme);

        jitemGestionInformes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionInformes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/historial1.png"))); // NOI18N
        jitemGestionInformes.setText("Gestionar informes");
        jitemGestionInformes.setPreferredSize(new java.awt.Dimension(200, 30));
        jMenu3.add(jitemGestionInformes);

        jitemCargarInformes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemCargarInformes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/categorias.png"))); // NOI18N
        jitemCargarInformes.setText("Cargar informe");
        jitemCargarInformes.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemCargarInformes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemCargarInformesActionPerformed(evt);
            }
        });
        jMenu3.add(jitemCargarInformes);

        jMenuBar1.add(jMenu3);

        jMenu5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/carrito.png"))); // NOI18N
        jMenu5.setText("Plan de Acción");
        jMenu5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jMenu5.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemGestionPlan.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionPlan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/anadir.png"))); // NOI18N
        jitemGestionPlan.setText("Gestionar plan de acción");
        jitemGestionPlan.setPreferredSize(new java.awt.Dimension(200, 30));
        jitemGestionPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemGestionPlanActionPerformed(evt);
            }
        });
        jMenu5.add(jitemGestionPlan);

        jitemConsultarPlan.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemConsultarPlan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuario.png"))); // NOI18N
        jitemConsultarPlan.setText("Consultar plan de acción");
        jitemConsultarPlan.setPreferredSize(new java.awt.Dimension(200, 30));
        jMenu5.add(jitemConsultarPlan);

        jMenuBar1.add(jMenu5);

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/reportes.png"))); // NOI18N
        jMenu6.setText("Evaluación ");
        jMenu6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jMenu6.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemGestionarEvaluacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemGestionarEvaluacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nuevo-producto.png"))); // NOI18N
        jitemGestionarEvaluacion.setText("Gestionar evaluacion y seguimiento");
        jitemGestionarEvaluacion.setPreferredSize(new java.awt.Dimension(340, 30));
        jMenu6.add(jitemGestionarEvaluacion);

        jitemConsultarEvaluacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemConsultarEvaluacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/configuraciones.png"))); // NOI18N
        jitemConsultarEvaluacion.setText("Consultar evaluacion y seguimiento");
        jitemConsultarEvaluacion.setPreferredSize(new java.awt.Dimension(340, 30));
        jMenu6.add(jitemConsultarEvaluacion);

        jMenuBar1.add(jMenu6);

        jitemCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar-sesion.png"))); // NOI18N
        jitemCerrarSesion.setText("Cerrar Sesión");
        jitemCerrarSesion.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jitemCerrarSesion.setPreferredSize(new java.awt.Dimension(200, 50));

        jitemCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jitemCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar-sesion.png"))); // NOI18N
        jitemCerrar.setText("Cerrar sesión");
        jitemCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jitemCerrarActionPerformed(evt);
            }
        });
        jitemCerrarSesion.add(jitemCerrar);

        jMenuBar1.add(jitemCerrarSesion);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jitemGestionRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemGestionRolesActionPerformed
        Roles mod = new Roles();
        ConsultasRoles modC = new ConsultasRoles();
        InterRoles frm = new InterRoles();
        CtrlRoles ctrl = new CtrlRoles(mod, modC, frm);

        jDesktopPane_menu.add(frm);
        ctrl.iniciar();
        frm.setVisible(true);
    }//GEN-LAST:event_jitemGestionRolesActionPerformed

    private void jitemCargarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemCargarUsuariosActionPerformed
        InterCargaUsuario interCargaUsuario = new InterCargaUsuario();
        jDesktopPane_menu.add(interCargaUsuario);
        interCargaUsuario.setVisible(true);

    }//GEN-LAST:event_jitemCargarUsuariosActionPerformed

    private void jitemResultadoGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemResultadoGeneralActionPerformed
        InterResultadosGenerales frm = new InterResultadosGenerales();
        CtrlResultadosGenerales ctrl = new CtrlResultadosGenerales(frm);
        jDesktopPane_menu.add(frm);
        frm.setVisible(true);
    }//GEN-LAST:event_jitemResultadoGeneralActionPerformed

    private void jitemCargarInformesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemCargarInformesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jitemCargarInformesActionPerformed

    private void jitemGestionUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemGestionUsuarioActionPerformed
        Usuario mod1 = new Usuario();
        ConsultasUsuario modC1 = new ConsultasUsuario();
        InterUsuario frm1 = new InterUsuario();
        CtrlUsuario ctrl1 = new CtrlUsuario(mod1, modC1, frm1);

        jDesktopPane_menu.add(frm1);
        ctrl1.iniciar();
        frm1.setVisible(true);
    }//GEN-LAST:event_jitemGestionUsuarioActionPerformed

    private void jitemCargarResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemCargarResultadosActionPerformed
        InterCargaResultados interCargaResultados = new InterCargaResultados();
        jDesktopPane_menu.add(interCargaResultados);
        interCargaResultados.setVisible(true);
    }//GEN-LAST:event_jitemCargarResultadosActionPerformed

    private void jitemGestionCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemGestionCuentaActionPerformed
        if (Usuario.usuarioActual == null) {
            JOptionPane.showMessageDialog(null, "No hay ningún usuario logueado actualmente.");
            return;
        }

        Usuario modCuenta = Usuario.usuarioActual;
        ConsultasUsuario modCCuenta = new ConsultasUsuario();
        InterCuenta frmCuenta = new InterCuenta();
        CtrlCuenta ctrlCuenta = new CtrlCuenta(modCuenta, modCCuenta, frmCuenta);

        jDesktopPane_menu.add(frmCuenta);
        ctrlCuenta.iniciar();
        frmCuenta.setVisible(true);
    }//GEN-LAST:event_jitemGestionCuentaActionPerformed

    private void jitemCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemCerrarActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la sesión actual?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Cerrar ventana actual
            this.dispose();

            // Abrir login
            com.mycompany.saberpro.Vista.frmLogin login = new com.mycompany.saberpro.Vista.frmLogin();
            login.setVisible(true);
            login.setLocationRelativeTo(null); // Centrar ventana
        }
    }//GEN-LAST:event_jitemCerrarActionPerformed

    private void jitemResultadoPersonalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemResultadoPersonalActionPerformed
        InterPersonal interPersonal = new InterPersonal();

        // Si el constructor detectó que NO hay pruebas, no mostramos nada
        if (!interPersonal.isTienePruebas()) {
            interPersonal.dispose(); // por si acaso
            return;
        }

        jDesktopPane_menu.removeAll();
        jDesktopPane_menu.repaint();
        jDesktopPane_menu.add(interPersonal);

        Dimension desktopSize = jDesktopPane_menu.getSize();
        Dimension frameSize = interPersonal.getSize();
        interPersonal.setLocation(
                (desktopSize.width - frameSize.width) / 2,
                (desktopSize.height - frameSize.height) / 2
        );

        interPersonal.setVisible(true);
    }//GEN-LAST:event_jitemResultadoPersonalActionPerformed

    private void jitemGestionPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemGestionPlanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jitemGestionPlanActionPerformed

    private void jitemGenerarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jitemGenerarInformeActionPerformed
        InterGenerarInforme inter = new InterGenerarInforme();
        CtrlGenerarInforme ctrl = new CtrlGenerarInforme(inter);
        jDesktopPane_menu.add(inter);   // usa el nombre real de tu desktop pane
        inter.setVisible(true);
    }//GEN-LAST:event_jitemGenerarInformeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new frmMenu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jitemCargarInformes;
    private javax.swing.JMenuItem jitemCargarResultados;
    private javax.swing.JMenuItem jitemCargarUsuarios;
    private javax.swing.JMenuItem jitemCerrar;
    private javax.swing.JMenu jitemCerrarSesion;
    private javax.swing.JMenuItem jitemConsultarEvaluacion;
    private javax.swing.JMenuItem jitemConsultarPlan;
    private javax.swing.JMenuItem jitemGenerarInforme;
    private javax.swing.JMenuItem jitemGestionCuenta;
    private javax.swing.JMenuItem jitemGestionInformes;
    private javax.swing.JMenuItem jitemGestionPlan;
    private javax.swing.JMenuItem jitemGestionRoles;
    private javax.swing.JMenuItem jitemGestionUsuario;
    private javax.swing.JMenuItem jitemGestionarEvaluacion;
    private javax.swing.JMenuItem jitemResultadoGeneral;
    private javax.swing.JMenuItem jitemResultadoPersonal;
    // End of variables declaration//GEN-END:variables
}
