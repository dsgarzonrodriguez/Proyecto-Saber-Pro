/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Vista.InterConsultasU;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.table.TableModel;

// JUNIT 4
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// MOCKITO
import static org.mockito.Mockito.*;

/**
 * Tests para CtrlConsultasUsuarios
 */
public class CtrlConsultasUsuariosTest {

    private InterConsultasU vista;
    private ConsultasUsuario modeloMock;
    private CtrlConsultasUsuarios ctrl;

    @Before
    public void setUp() throws Exception {
        // Vista real (solo componentes Swing)
        vista = new InterConsultasU();

        // Mock del modelo (no queremos ir a la BD)
        modeloMock = mock(ConsultasUsuario.class);

        // ResultSet vacío para el constructor (cargarCombos)
        ResultSet rsVacio = mock(ResultSet.class);
        when(rsVacio.next()).thenReturn(false);
        when(modeloMock.obtenerRoles()).thenReturn(rsVacio);

        // Crear el controlador (esto llama a cargarCombos)
        ctrl = new CtrlConsultasUsuarios(vista, modeloMock);
    }

    // ---------------------------------------------------------
    // TEST 1: limpiarFiltros()
    // ---------------------------------------------------------
    @Test
    public void testLimpiarFiltros() {
        // Preparar estado "sucio"
        vista.txtBuscar.setText("algo");
        vista.boxRol.addItem("Todos");
        vista.boxRol.addItem("Estudiante");
        vista.boxRol.setSelectedIndex(1);

        vista.boxEstado.addItem("Todos");
        vista.boxEstado.addItem("Habilitado");
        vista.boxEstado.setSelectedIndex(1);

        Date ahora = new Date();
        vista.dateInicio.setDate(ahora);
        vista.dateFin.setDate(ahora);

        // Ejecutar método privado limpiarFiltros() mediante reflexión
        try {
            Method m = CtrlConsultasUsuarios.class.getDeclaredMethod("limpiarFiltros");
            m.setAccessible(true);
            m.invoke(ctrl);
        } catch (Exception e) {
            fail("Error invocando limpiarFiltros por reflexión: " + e.getMessage());
        }

        // Verificar
        assertEquals("",
                vista.txtBuscar.getText());
        assertEquals(0, vista.boxRol.getSelectedIndex());
        assertEquals(0, vista.boxEstado.getSelectedIndex());
        assertNull(vista.dateInicio.getDate());
        assertNull(vista.dateFin.getDate());
    }

    // ---------------------------------------------------------
    // TEST 2: llenarTablaDesdeResultSet()
    // ---------------------------------------------------------
    @Test
    public void testLlenarTablaDesdeResultSet() throws SQLException {
        // ResultSet de prueba con una fila
        ResultSet rs = mock(ResultSet.class);

        // first next() -> true (hay fila), second -> false (fin)
        when(rs.next()).thenReturn(true, false);

        when(rs.getObject("nombre")).thenReturn("Luna");
        when(rs.getObject("apellido")).thenReturn("Sanchez");
        when(rs.getObject("telefono")).thenReturn("3102345678");
        when(rs.getObject("cc")).thenReturn("1123224544");
        when(rs.getObject("correo")).thenReturn("prueba@example.com");
        when(rs.getObject("habilitado")).thenReturn(true);
        when(rs.getObject("rol")).thenReturn("Estudiante");
        when(rs.getObject("fecha_inicio")).thenReturn(
                Timestamp.valueOf("2024-01-01 10:00:00"));
        when(rs.getObject("fecha_fin")).thenReturn(null);

        // Invocar método privado llenarTablaDesdeResultSet(ResultSet)
        try {
            Method m = CtrlConsultasUsuarios.class
                    .getDeclaredMethod("llenarTablaDesdeResultSet", ResultSet.class);
            m.setAccessible(true);
            m.invoke(ctrl, rs);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error invocando llenarTablaDesdeResultSet por reflexión: " + e.getMessage());
        }

        // Verificar contenido de la JTable de la vista
        TableModel model = vista.jTable.getModel();

        // Una fila
        assertEquals(1, model.getRowCount());
        // 9 columnas: Nombre, Apellido, Telefono, CC, Correo,
        // Habilitado, Rol, Fecha inicio, Fecha fin
        assertEquals(9, model.getColumnCount());

        assertEquals("Luna", model.getValueAt(0, 0));
        assertEquals("Sanchez", model.getValueAt(0, 1));
        assertEquals("3102345678", model.getValueAt(0, 2));
        assertEquals("1123224544", model.getValueAt(0, 3));
        assertEquals("prueba@example.com", model.getValueAt(0, 4));

        // Habilitado debe haberse transformado a texto
        assertEquals("Habilitado", model.getValueAt(0, 5));
        assertEquals("Estudiante", model.getValueAt(0, 6));
        assertNotNull(model.getValueAt(0, 7)); // fecha inicio
        assertNull(model.getValueAt(0, 8));    // fecha fin
    }
}
