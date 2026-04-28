/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Modelo.Roles;
import com.mycompany.saberpro.Modelo.Usuario;
import com.mycompany.saberpro.Vista.InterUsuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author juanf
 */
public class CtrlUsuario implements ActionListener {

    private Usuario mod1;
    private ConsultasUsuario modC1;
    private InterUsuario frm1;

    public CtrlUsuario() {
        // Constructor vacío para login
    }

    public boolean loginUser(Usuario objeto) {
        ConsultasUsuario modC = new ConsultasUsuario();

        try {
            // valida correo + contraseña
            if (modC.login(objeto)) {

                if (!objeto.isHabilitado()) {
                    JOptionPane.showMessageDialog(null, "La cuenta está deshabilitada.");
                    return false;
                }

                Usuario.usuarioActual = objeto; // si lo usas en el resto del sistema
                return true;

            } else {
                JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error loginUser: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al intentar iniciar sesión.");
            return false;
        }
    }

    // Constructor principal para la pantalla de usuarios
    public CtrlUsuario(Usuario mod1, ConsultasUsuario modC1, InterUsuario frm1) {
        this.mod1 = mod1;
        this.modC1 = modC1;
        this.frm1 = frm1;

        // OJO: YA NO escuchamos btnAgregar ni btnModificar aquí.
        // Esos los maneja InterUsuario con sus propios actionPerformed.
        this.frm1.btnEliminar.addActionListener(this);
        this.frm1.btnBuscar.addActionListener(this);
        this.frm1.btnActivar.addActionListener(this);
        this.frm1.btnLimpiar.addActionListener(this);
    }

    public void iniciar() {
        frm1.txtId.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // ================= ELIMINAR / DESACTIVAR =================
        if (e.getSource() == frm1.btnEliminar) {
            if (frm1.txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un código para inhabilitar el usuario.");
                return;
            }

            mod1.setId_usuario(Integer.parseInt(frm1.txtCodigo.getText()));
            Roles rolSeleccionado = (Roles) frm1.cbxRol.getSelectedItem();
            mod1.setRol(rolSeleccionado);

            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro de que desea inhabilitar este usuario?",
                    "Confirmar inhabilitación", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (modC1.eliminar(mod1)) {
                    JOptionPane.showMessageDialog(null, "Usuario inhabilitado correctamente.");
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al inhabilitar el usuario.");
                }
            }
        }

        // ================= LIMPIAR CAMPOS =================
        if (e.getSource() == frm1.btnLimpiar) {
            limpiar();
        }

        // ================= BUSCAR USUARIO =================
        if (e.getSource() == frm1.btnBuscar) {
            // Limpiar valores previos en el modelo
            mod1.setId_usuario(0);
            mod1.setCorreo(null);

            // Buscar por ID o por correo
            if (!frm1.txtCodigo.getText().isEmpty()) {
                mod1.setId_usuario(Integer.parseInt(frm1.txtCodigo.getText()));
            } else if (!frm1.txtCorreo.getText().isEmpty()) {
                mod1.setCorreo(frm1.txtCorreo.getText());
            } else {
                JOptionPane.showMessageDialog(null, "Debe ingresar un ID o un correo para buscar.");
                return;
            }

            // Buscar el usuario
            if (modC1.buscar(mod1)) {
                frm1.txtCodigo.setText(String.valueOf(mod1.getId_usuario()));
                frm1.txtNombre.setText(mod1.getNombre());
                frm1.txtApellido.setText(mod1.getApellido());
                frm1.txtTelefono.setText(mod1.getTelefono());
                frm1.txtCC.setText(mod1.getCc());
                frm1.txtCorreo.setText(mod1.getCorreo());
                frm1.txtContraseña.setText(mod1.getContrasena());

                // Seleccionar el rol correspondiente en el combo
                for (int i = 0; i < frm1.cbxRol.getItemCount(); i++) {
                    Roles rolCombo = (Roles) frm1.cbxRol.getItemAt(i);
                    if (rolCombo.getId_roles() == mod1.getRol().getId_roles()) {
                        frm1.cbxRol.setSelectedIndex(i);
                        break;
                    }
                }

                // Habilitado / inhabilitado
                if (mod1.isHabilitado()) {
                    setCamposEditable(true);
                    frm1.btnActivar.setVisible(false);
                    frm1.btnEliminar.setVisible(true);
                } else {
                    setCamposEditable(false);
                    frm1.btnEliminar.setVisible(false);
                    frm1.btnActivar.setVisible(true);
                }

            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún registro con los datos proporcionados.");
                limpiar();
            }
        }

        // ================= ACTIVAR USUARIO =================
        if (e.getSource() == frm1.btnActivar) {
            if (frm1.txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un código primero.");
                return;
            }

            mod1.setId_usuario(Integer.parseInt(frm1.txtCodigo.getText()));

            if (modC1.habilitar(mod1)) {
                JOptionPane.showMessageDialog(null, "Usuario habilitado correctamente.");
                setCamposEditable(true);
                frm1.btnActivar.setVisible(false);
                frm1.btnEliminar.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Error al habilitar el usuario.");
            }
        }
    }

    private void setCamposEditable(boolean editable) {
        frm1.txtNombre.setEditable(editable);
        frm1.txtApellido.setEditable(editable);
        frm1.txtTelefono.setEditable(editable);
        frm1.txtCC.setEditable(editable);
        frm1.txtCorreo.setEditable(editable);
        frm1.txtContraseña.setEditable(editable);
        frm1.cbxRol.setEnabled(editable);

        java.awt.Color color = editable ? java.awt.Color.WHITE : new java.awt.Color(230, 230, 230);
        frm1.txtNombre.setBackground(color);
        frm1.txtApellido.setBackground(color);
        frm1.txtTelefono.setBackground(color);
        frm1.txtCC.setBackground(color);
        frm1.txtCorreo.setBackground(color);
        frm1.txtContraseña.setBackground(color);
    }

    public void limpiar() {
        frm1.txtCodigo.setText(null);
        frm1.txtNombre.setText(null);
        frm1.txtApellido.setText(null);
        frm1.txtTelefono.setText(null);
        frm1.txtCC.setText(null);
        frm1.txtId.setText(null);
        frm1.txtCorreo.setText(null);
        frm1.txtContraseña.setText(null);
        frm1.cbxRol.setSelectedIndex(0);

        setCamposEditable(true);

        frm1.btnActivar.setVisible(false);
        frm1.btnEliminar.setVisible(true);
    }

}
