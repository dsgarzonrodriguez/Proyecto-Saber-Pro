package com.saberpro.adapters.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.saberpro.entities.FiltrosConsulta;
import com.saberpro.entities.Usuario;
import com.saberpro.infrastructure.persistence.repositories.InformePortImpl;
import com.saberpro.infrastructure.services.InformeGatewayImpl;
import com.saberpro.usecases.reports.GenerarInformeIAUseCase;
import com.saberpro.usecases.reports.GuardarInformePDFUseCase;

import java.io.File;

/**
 * Controlador JavaFX para la vista de Generar Informe con IA.
 *
 * Arquitectura Clean:
 *   Vista (FXML)  →  InformeController  →  GenerarInformeIAUseCase
 *                                        →  GuardarInformePDFUseCase
 *                                              ↓
 *                                         InformePort  (consulta BD)
 *                                         InformeGateway (llama Gemini / guarda PDF)
 */
public class InformeController {

    // ──────────────────────────────────────────────────────────
    // Campos FXML  (nombra igual en tu archivo .fxml)
    // ──────────────────────────────────────────────────────────
    @FXML private ComboBox<String> cbxAnio;
    @FXML private ComboBox<String> cbxSemestre;
    @FXML private ComboBox<String> cbxPrograma;
    @FXML private ComboBox<String> cbxCiudad;

    @FXML private TextArea         txtInforme;
    @FXML private Button           btnGenerar;
    @FXML private Button           btnLimpiar;
    @FXML private Button           btnGuardar;

    /** Spinner / indicador de carga.  Ponlo visible=false en el FXML por defecto. */
    @FXML private ProgressIndicator progressIndicator;

    // ──────────────────────────────────────────────────────────
    // Casos de uso (instanciados aquí; inyéctalos si usas DI)
    // ──────────────────────────────────────────────────────────
    private final InformePortImpl         informePort    = new InformePortImpl();
    private final InformeGatewayImpl      informeGateway = new InformeGatewayImpl();
    private final GenerarInformeIAUseCase generarUseCase =
            new GenerarInformeIAUseCase(informePort, informeGateway);
    private final GuardarInformePDFUseCase guardarUseCase =
            new GuardarInformePDFUseCase(informeGateway);

    // ──────────────────────────────────────────────────────────
    // Inicialización
    // ──────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Semestre es estático; el resto se puede poblar desde BD (ver método comentado abajo).
        cbxSemestre.setItems(FXCollections.observableArrayList("Todos", "1", "2"));
        cbxSemestre.setValue("Todos");

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        /*
         * Descomenta estas líneas si quieres poblar los combos desde la BD:
         *
         * cargarComboDesdeVista(cbxAnio,
         *     "SELECT DISTINCT ano FROM vista_resultados_detalle ORDER BY 1 DESC");
         * cargarComboDesdeVista(cbxPrograma,
         *     "SELECT DISTINCT programa FROM vista_resultados_detalle ORDER BY 1");
         * cargarComboDesdeVista(cbxCiudad,
         *     "SELECT DISTINCT ciudad FROM vista_resultados_detalle ORDER BY 1");
         */
    }

    // ──────────────────────────────────────────────────────────
    // Acción: Generar informe con IA (Gemini)
    // ──────────────────────────────────────────────────────────
    @FXML
    private void onGenerar() {
        FiltrosConsulta filtros = leerFiltros();
        if (filtros == null) return; // error de validación ya notificado

        String nombreUsuario = "";
        String rolUsuario    = "Directivo";

        Usuario usuarioActual = SesionActual.getUsuario();
        if (usuarioActual != null) {
            nombreUsuario = usuarioActual.getNombre() + " " + usuarioActual.getApellido();
            if (usuarioActual.getRol() != null && usuarioActual.getRol().getNombre() != null) {
                rolUsuario = usuarioActual.getRol().getNombre();
            }
        }

        final String finalNombre = nombreUsuario;
        final String finalRol    = rolUsuario;

        setUiCargando(true);

        Task<String> tarea = new Task<>() {
            @Override
            protected String call() throws Exception {
                return generarUseCase.ejecutar(filtros, finalNombre, finalRol);
            }
        };

        tarea.setOnSucceeded(e -> {
            txtInforme.setText(tarea.getValue());
            setUiCargando(false);
        });

        tarea.setOnFailed(e -> {
            Throwable ex = tarea.getException();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error al generar informe",
                    ex != null ? ex.getMessage() : "Error desconocido.");
            setUiCargando(false);
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    @FXML
    private void onLimpiar() {
        cbxAnio.setValue(null);
        cbxSemestre.setValue("Todos");
        cbxPrograma.setValue(null);
        cbxCiudad.setValue(null);
        txtInforme.clear();
    }

    // ──────────────────────────────────────────────────────────
    // Acción: Guardar informe como PDF
    // ──────────────────────────────────────────────────────────
    @FXML
    private void onGuardar() {
        String texto = txtInforme.getText();

        // Validación de precondición en el use case
        try {
            guardarUseCase.ejecutar(texto);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin informe", e.getMessage());
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar informe como PDF");
        chooser.setInitialFileName("informe-saberpro.pdf");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        File archivo = chooser.showSaveDialog(stage);
        if (archivo == null) return; // usuario canceló

        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        final String rutaFinal  = ruta;
        final String textoFinal = texto;

        setUiCargando(true);

        Task<Void> tareaGuardar = new Task<>() {
            @Override
            protected Void call() throws Exception {
                informeGateway.guardarInformePDF(textoFinal, rutaFinal);
                return null;
            }
        };

        tareaGuardar.setOnSucceeded(e -> {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "PDF guardado",
                    "Informe guardado en:\n" + rutaFinal);
            setUiCargando(false);
        });

        tareaGuardar.setOnFailed(e -> {
            Throwable ex = tareaGuardar.getException();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error al guardar PDF",
                    ex != null ? ex.getMessage() : "Error desconocido.");
            setUiCargando(false);
        });

        Thread hilo = new Thread(tareaGuardar);
        hilo.setDaemon(true);
        hilo.start();
    }

    // ──────────────────────────────────────────────────────────
    // Helpers privados
    // ──────────────────────────────────────────────────────────

    /**
     * Lee los combos y construye un {@link FiltrosConsulta}.
     * Devuelve {@code null} si hay un error de validación (muestra alerta interna).
     */
    private FiltrosConsulta leerFiltros() {
        FiltrosConsulta filtros = new FiltrosConsulta();

        String anioStr = cbxAnio.getValue();
        if (anioStr != null && !anioStr.isBlank() && !anioStr.equalsIgnoreCase("Todos")) {
            try {
                filtros.setAnio(Integer.parseInt(anioStr.trim()));
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Año inválido",
                        "El año debe ser un número entero.");
                return null;
            }
        }

        String semStr = cbxSemestre.getValue();
        if (semStr != null && !semStr.equalsIgnoreCase("Todos")) {
            filtros.setSemestre(Integer.parseInt(semStr));
        }

        String programa = cbxPrograma.getValue();
        if (programa != null && !programa.isBlank() && !programa.equalsIgnoreCase("Todos")) {
            filtros.setPrograma(programa.trim());
        }

        String ciudad = cbxCiudad.getValue();
        if (ciudad != null && !ciudad.isBlank() && !ciudad.equalsIgnoreCase("Todos")) {
            filtros.setCiudad(ciudad.trim());
        }

        return filtros;
    }

    /** Habilita/deshabilita botones y muestra el spinner de carga. */
    private void setUiCargando(boolean cargando) {
        Platform.runLater(() -> {
            btnGenerar.setDisable(cargando);
            btnGuardar.setDisable(cargando);
            btnLimpiar.setDisable(cargando);
            if (progressIndicator != null) {
                progressIndicator.setVisible(cargando);
            }
        });
    }

    /** Muestra una alerta de JavaFX de forma segura en el hilo de la UI. */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    /*
     * ──────────────────────────────────────────────────────────
     * Poblar ComboBox desde la BD (opcional)
     * ──────────────────────────────────────────────────────────
     *
     * private void cargarComboDesdeVista(ComboBox<String> combo, String sql) {
     *     try (Connection con = new saberPro.infrastructure.config.PostgresConexion().getConexion();
     *          PreparedStatement ps = con.prepareStatement(sql);
     *          ResultSet rs = ps.executeQuery()) {
     *
     *         List<String> items = new java.util.ArrayList<>();
     *         items.add("Todos");
     *         while (rs.next()) {
     *             items.add(rs.getString(1));
     *         }
     *         combo.setItems(FXCollections.observableArrayList(items));
     *         combo.setValue("Todos");
     *
     *     } catch (Exception ex) {
     *         mostrarAlerta(Alert.AlertType.ERROR, "Error",
     *                 "No se pudo cargar el combo: " + ex.getMessage());
     *     }
     * }
     */
}