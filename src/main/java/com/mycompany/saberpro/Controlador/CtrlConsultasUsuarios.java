/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Vista.InterConsultasU;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author juanf
 */
public class CtrlConsultasUsuarios implements ActionListener {

    private InterConsultasU vista;
    private ConsultasUsuario modelo;

    public CtrlConsultasUsuarios(InterConsultasU vista, ConsultasUsuario modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.vista.btnBuscar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        cargarCombos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnBuscar) {
            buscarUsuarios();
        } else if (e.getSource() == vista.btnLimpiar) {
            limpiarFiltros();
        }
    }

    private void cargarCombos() {
        // Rol
        vista.boxRol.removeAllItems();
        vista.boxRol.addItem("Todos");
        try (ResultSet rs = modelo.obtenerRoles()) {
            while (rs.next()) {
                vista.boxRol.addItem(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.err.println("Error cargar roles: " + e.getMessage());
        }

        // Estado (esto NO se trae de DB, lo inventas aquí)
        vista.boxEstado.removeAllItems();
        vista.boxEstado.addItem("Todos");
        vista.boxEstado.addItem("Habilitado");
        vista.boxEstado.addItem("Inhabilitado");
    }

    private void buscarUsuarios() {
        String texto = vista.txtBuscar.getText().trim();

        String rol = null;
        if (vista.boxRol.getSelectedItem() != null) {
            rol = vista.boxRol.getSelectedItem().toString();
        }

        String estado = null;
        if (vista.boxEstado.getSelectedItem() != null) {
            estado = vista.boxEstado.getSelectedItem().toString();
        }

        // Convertir fechas a Timestamp (si el componente devuelve java.util.Date)
        Timestamp inicioTs = null;
        Timestamp finTs = null;
        try {
            Date inicio = null;
            Date fin = null;
            // Si usas JDateChooser:
            try {
                inicio = (Date) vista.dateInicio.getDate();
            } catch (Exception ex) {
                // si no existe o no es JDateChooser, se mantiene null
                inicio = null;
            }
            try {
                fin = (Date) vista.dateFin.getDate();
            } catch (Exception ex) {
                fin = null;
            }

            if (inicio != null) {
                inicioTs = new Timestamp(inicio.getTime());
            }
            if (fin != null) {
                finTs = new Timestamp(fin.getTime());
            }
        } catch (Exception ex) {
            // Si tu componente de fecha es JTextField deberías parsear la cadena aquí.
            inicioTs = null;
            finTs = null;
        }

        ResultSet rs = null;
        try {
            rs = modelo.consultarUsuarios(texto, rol, estado, inicioTs, finTs, false);
            llenarTablaDesdeResultSet(rs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al consultar usuarios: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            // cerrar ResultSet aquí si modelo no lo cerró (si el modelo abrió la conexión debería cerrarla ahí)
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error cerrando ResultSet: " + ex.getMessage());
            }
        }
    }

    // Limpia filtros y recarga todos los usuarios
    private void limpiarFiltros() {
        vista.txtBuscar.setText("");
        vista.boxRol.setSelectedIndex(0);     // asume que 0 = "Todos"
        vista.boxEstado.setSelectedIndex(0);  // asume que 0 = "Todos"

        // Si usas JDateChooser:
        try {
            vista.dateInicio.setDate(null);
            vista.dateFin.setDate(null);
        } catch (Exception ex) {
            // si no es JDateChooser ignora
        }

        // Recargar toda la tabla (llama a buscar con todo vacío)
        buscarUsuarios();
    }

    // Llena la JTable con el ResultSet (orden de columnas que pediste)
    private void llenarTablaDesdeResultSet(ResultSet rs) throws SQLException {
        // Columnas: Nombre, Apellido, Telefono, CC, Correo, Habilitado, Rol, Fecha inicio, Fecha fin
        String[] columnas = {"Nombre", "Apellido", "Teléfono", "CC", "Correo", "Habilitado", "Rol", "Fecha inicio", "Fecha fin"};
        DefaultTableModel model = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla solo lectura
            }
        };

        if (rs != null) {
            while (rs.next()) {
                Object nombre = rs.getObject("nombre");
                Object apellido = rs.getObject("apellido");
                Object telefono = rs.getObject("telefono");
                Object cc = rs.getObject("cc");
                Object correo = rs.getObject("correo");
                Object habilitadoObj = rs.getObject("habilitado");
                Object rol = rs.getObject("rol");
                Object fechaInicio = rs.getObject("fecha_inicio");
                Object fechaFin = rs.getObject("fecha_fin");

                // Normalizar habilitado a cadena legible (si tu vista devuelve 'Habilitado' ya no haría falta)
                String habilitadoStr = "";
                if (habilitadoObj instanceof Boolean) {
                    habilitadoStr = ((Boolean) habilitadoObj) ? "Habilitado" : "Inhabilitado";
                } else if (habilitadoObj != null) {
                    habilitadoStr = habilitadoObj.toString();
                }

                Object[] fila = {
                    nombre, apellido, telefono, cc, correo, habilitadoStr, rol, fechaInicio, fechaFin
                };
                model.addRow(fila);
            }
        }

        vista.jTable.setModel(model);

    }
}
