package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Vista.InterGenerarInforme;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

// JUnit 4
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Mockito
import static org.mockito.Mockito.*;

/**
 * Tests para CtrlGenerarInforme
 */
public class CtrlGenerarInformeTest {

    private InterGenerarInforme vistaMock;
    private CtrlGenerarInforme ctrl;

    // Componentes reales para poder revisar los listeners
    private JButton btnGenerar;
    private JButton btnLimpiar;
    private JButton btnGuardar;
    private JLabel lblCargando;
    private JProgressBar barraCarga;
    private JTextArea txtInforme;

    @Before
    public void setUp() {
        // Mock de la vista
        vistaMock = mock(InterGenerarInforme.class);

        // Componentes Swing REALES (no mocks)
        btnGenerar = new JButton();
        btnLimpiar = new JButton();
        btnGuardar = new JButton();
        lblCargando = new JLabel();
        barraCarga = new JProgressBar();
        txtInforme = new JTextArea();

        // Programamos la vista para que devuelva estos componentes
        when(vistaMock.getBtnGenerar()).thenReturn(btnGenerar);
        when(vistaMock.getBtnLimpiar()).thenReturn(btnLimpiar);
        when(vistaMock.getBtnGuardar()).thenReturn(btnGuardar);
        when(vistaMock.getLabel()).thenReturn(lblCargando);
        when(vistaMock.getCarga()).thenReturn(barraCarga);
        when(vistaMock.getTxtInforme()).thenReturn(txtInforme);

        // Creamos el controlador (esto registra los listeners)
        ctrl = new CtrlGenerarInforme(vistaMock);
    }

    /**
     * Verifica que el constructor registra UN listener en cada uno de los
     * botones: generar, limpiar y guardar.
     */
    @Test
    public void testConstructorRegistraListeners() {
        // Cada botón debería tener exactamente 1 ActionListener
        assertEquals(1, btnGenerar.getActionListeners().length);
        assertEquals(1, btnLimpiar.getActionListeners().length);
        assertEquals(1, btnGuardar.getActionListeners().length);
    }

    /**
     * Simula hacer clic en el botón Limpiar y verifica que se llama a
     * vista.limpiarFiltrosYTexto().
     */
    @Test
    public void testClickBtnLimpiarLlamaVistaLimpiar() {
        // Obtenemos el listener que el controlador registró
        ActionListener listenerLimpiar = btnLimpiar.getActionListeners()[0];

        // Simular un click
        ActionEvent evt = new ActionEvent(btnLimpiar, ActionEvent.ACTION_PERFORMED, "click");
        listenerLimpiar.actionPerformed(evt);

        // Verificamos que la vista recibió la llamada
        verify(vistaMock, times(1)).limpiarFiltrosYTexto();
    }

    /**
     * Simula hacer clic en el botón Guardar y verifica que se llama a
     * vista.guardarInformeComoPDF().
     */
    @Test
    public void testClickBtnGuardarLlamaVistaGuardarPDF() {
        // Obtenemos el listener que el controlador registró
        ActionListener listenerGuardar = btnGuardar.getActionListeners()[0];

        // Simular un click
        ActionEvent evt = new ActionEvent(btnGuardar, ActionEvent.ACTION_PERFORMED, "click");
        listenerGuardar.actionPerformed(evt);

        // Verificamos que la vista recibió la llamada
        verify(vistaMock, times(1)).guardarInformeComoPDF();
    }
}
