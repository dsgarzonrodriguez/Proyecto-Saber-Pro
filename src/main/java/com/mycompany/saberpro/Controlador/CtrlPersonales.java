package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.Conexion;
import com.mycompany.saberpro.Modelo.EstudianteSeleccionado;
import com.mycompany.saberpro.Vista.InterPersonales;
import com.mycompany.saberpro.Vista.InterResultadoPersonal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CtrlPersonales {

    private final InterPersonales vista;
    private final DefaultTableModel modeloTabla;

    public CtrlPersonales(InterPersonales vista) {
        this.vista = vista;

        // Modelo con las MISMAS columnas que la JTable del form
        this.modeloTabla = new DefaultTableModel(
                new Object[]{"Nombre", "Apellido", "CC", "Número de registro", "Año", "Semestre"}, 0
        );
        this.vista.getTablaEstudiantes().setModel(modeloTabla);

        // Listeners
        if (vista.getBtnBuscar() != null) {
            vista.getBtnBuscar().addActionListener(e -> buscarEstudiantes());
        }
        if (vista.getBtnLimpiar() != null) {
            vista.getBtnLimpiar().addActionListener(e -> limpiar());
        }
        if (vista.getBtnSeleccionar() != null) {
            vista.getBtnSeleccionar().addActionListener(e -> seleccionar());
        }
    }

    // =========================
    //   BÚSQUEDA EN LA TABLA
    // =========================
    private void buscarEstudiantes() {
        String nombre = vista.getTxtEstudiante().getText().trim();
        String cc = vista.getTxtCC().getText().trim();

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        if (nombre.isEmpty() && cc.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Ingrese al menos un criterio (Estudiante o CC).",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT nombre, apellido, cc, numero_registro, ano, semestre "
                + "FROM vista_estudiantes_pruebas " // <-- nombre de la vista
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (!nombre.isEmpty()) {
            sql.append(" AND (LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ?) ");
            String patron = "%" + nombre.toLowerCase() + "%";
            params.add(patron);
            params.add(patron);
        }

        if (!cc.isEmpty()) {
            sql.append(" AND cc LIKE ? ");
            params.add(cc + "%");
        }

        sql.append(" ORDER BY apellido, nombre, ano DESC, semestre DESC");

        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    Object[] fila = new Object[]{
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cc"),
                        rs.getString("numero_registro"),
                        rs.getInt("ano"),
                        rs.getInt("semestre")
                    };
                    modeloTabla.addRow(fila);
                }

                if (!hayDatos) {
                    JOptionPane.showMessageDialog(vista,
                            "No se encontraron estudiantes con esos criterios.",
                            "Sin resultados",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error al buscar estudiantes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================
    //   LIMPIAR CAMPOS Y TABLA
    // =========================
    private void limpiar() {
        vista.getTxtEstudiante().setText("");
        vista.getTxtCC().setText("");
        modeloTabla.setRowCount(0);
        EstudianteSeleccionado.limpiar();
    }

    // =========================
    //   SELECCIONAR ESTUDIANTE
    // =========================
    private void seleccionar() {
        JTable tabla = vista.getTablaEstudiantes();
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un estudiante de la tabla.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 0=Nombre, 1=Apellido, 2=CC, 3=Número de registro, 4=Año, 5=Semestre
        String cc = tabla.getValueAt(fila, 2).toString();
        String numeroRegistro = tabla.getValueAt(fila, 3).toString();

        Integer anio = null;
        Integer semestre = null;

        Object valAnio = tabla.getValueAt(fila, 4);
        Object valSem = tabla.getValueAt(fila, 5);

        if (valAnio != null && !valAnio.toString().trim().isEmpty()) {
            try {
                anio = Integer.valueOf(valAnio.toString().trim());
            } catch (NumberFormatException e) {
                // lo dejamos en null
            }
        }
        if (valSem != null && !valSem.toString().trim().isEmpty()) {
            try {
                semestre = Integer.valueOf(valSem.toString().trim());
            } catch (NumberFormatException e) {
                // lo dejamos en null
            }
        }

        // Aquí usamos la versión con 4 parámetros
        EstudianteSeleccionado.setSeleccion(cc, numeroRegistro, anio, semestre);

        // Obtenemos el DesktopPane
        JDesktopPane desktop = vista.getDesktopPane();

        // Cerrar cualquier InterResultadoPersonal ya abierto
        for (javax.swing.JInternalFrame f : desktop.getAllFrames()) {
            if (f instanceof InterResultadoPersonal) {
                f.dispose();
            }
        }

        // Abrir uno nuevo con la selección actual
        InterResultadoPersonal inter = new InterResultadoPersonal();
        desktop.add(inter);
        inter.setVisible(true);
        inter.toFront();

        // Opcional: cerrar la ventana de búsqueda
        vista.dispose();
    }
}
