package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.Usuario;
import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Vista.InterCuenta;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class CtrlCuenta implements ActionListener {

    private Usuario usuario;
    private ConsultasUsuario consultas;
    private InterCuenta vista;

    public CtrlCuenta(Usuario usuario, ConsultasUsuario consultas, InterCuenta vista) {
        this.usuario = usuario;
        this.consultas = consultas;
        this.vista = vista;
        this.vista.btnActualizar.addActionListener(this);
    }

    // Inicializa el formulario con los datos del usuario logueado
    public void iniciar() {
        vista.txtCodigo.setText(String.valueOf(usuario.getId_usuario()));
        vista.txtNombre.setText(usuario.getNombre());
        vista.txtApellido.setText(usuario.getApellido());
        vista.txtTelefono.setText(usuario.getTelefono());
        vista.txtCC.setText(usuario.getCc());
        vista.txtCorreo.setText(usuario.getCorreo());
        vista.txtContrasena.setText(usuario.getContrasena());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnActualizar) {
             String nuevaPass = vista.txtContrasena.getText();

            // 🔹 Validación de la contraseña antes de continuar
            if (!nuevaPass.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.!@#$%&*\\-_])[A-Za-z\\d.!@#$%&*\\-_]{8,}$")) {
                JOptionPane.showMessageDialog(vista,
                        "La contraseña debe tener al menos 8 caracteres,\n"
                        + "una letra, un número y un carácter especial permitido (.!@#$%&*-_).");
                return;
            }

            // Actualizar datos desde la vista al modelo
            usuario.setNombre(vista.txtNombre.getText());
            usuario.setApellido(vista.txtApellido.getText());
            usuario.setTelefono(vista.txtCC.getText());
            usuario.setCc(vista.txtCC.getText());
            usuario.setCorreo(vista.txtCorreo.getText());
            usuario.setContrasena(vista.txtContrasena.getText());

            // Llamar al método de modificación
            
            if (consultas.modificar(usuario, usuario.getId_usuario())) {
                JOptionPane.showMessageDialog(null, "Cuenta actualizada correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar la cuenta.");
            }
        }
    }
}
