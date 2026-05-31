package com.saberpro.adapters.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.saberpro.entities.Usuario;

public class MenuController {

    // ── Bienvenida ──────────────────────────────────────────────────────────
    @FXML private Label lblBienvenida;

    // ── Menú Usuario ────────────────────────────────────────────────────────
    @FXML private MenuItem menuGestionCuenta;
    @FXML private MenuItem menuGestionUsuario;
    @FXML private MenuItem menuGestionRoles;
    @FXML private MenuItem menuCargarUsuarios;

    // ── Menú Resultados ─────────────────────────────────────────────────────
    @FXML private MenuItem menuResultadoPersonal;
    @FXML private MenuItem menuResultadoGeneral;
    @FXML private MenuItem menuCargarResultados;

    // ── Menú Informes ───────────────────────────────────────────────────────
    @FXML private Menu     menuInformes;
    @FXML private MenuItem menuGenerarInforme;
    @FXML private MenuItem menuGestionInformes;

    // ── Menú Plan de Acción ─────────────────────────────────────────────────
    @FXML private Menu     menuPlanAccion;
    @FXML private MenuItem menuConsultarPlan;

    // ── Menú Evaluación ─────────────────────────────────────────────────────
    @FXML private Menu     menuEvaluacion;

    // ────────────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        Usuario usuario = SesionActual.getUsuario();

        if (usuario == null) return;

        // Bienvenida
        lblBienvenida.setText("Bienvenido, " + usuario.getNombre()
                + " " + usuario.getApellido()
                + "  |  Rol: " + (usuario.getRol() != null
                        ? usuario.getRol().getNombre() : ""));

        configurarPermisos(usuario);
    }

    // ── Permisos por rol ────────────────────────────────────────────────────

    private void configurarPermisos(Usuario usuario) {
        ocultarTodo();

        if (usuario.getRol() == null) return;
        
        String nombre = usuario.getRol().getNombre();
        if (nombre == null) return; // <-- agrega esto

        String rol = nombre.toLowerCase()
                .replace("á","a").replace("é","e")
                .replace("í","i").replace("ó","o").replace("ú","u");

        switch (rol) {
            case "estudiante" -> {
                menuGestionCuenta.setVisible(true);
                menuResultadoPersonal.setVisible(true);
            }
            case "profesor" -> {
                menuGestionCuenta.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
            }
            case "administrador" -> {
                menuGestionCuenta.setVisible(true);
                menuGestionUsuario.setVisible(true);
                menuGestionRoles.setVisible(true);
                menuCargarUsuarios.setVisible(true);
                menuResultadoPersonal.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuCargarResultados.setVisible(true);
                menuInformes.setVisible(true);
                menuGenerarInforme.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            case "decano" -> {
                menuGestionCuenta.setVisible(true);
                menuGestionUsuario.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuInformes.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            case "director de programa" -> {
                menuGestionCuenta.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuCargarResultados.setVisible(true);
                menuInformes.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            case "coordinador saber pro" -> {
                menuGestionCuenta.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuCargarResultados.setVisible(true);
                menuInformes.setVisible(true);
                menuGenerarInforme.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            case "comite de programa" -> {
                menuGestionCuenta.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuCargarResultados.setVisible(true);
                menuInformes.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            case "secretaria de acreditacion" -> {
                menuGestionCuenta.setVisible(true);
                menuGestionUsuario.setVisible(true);
                menuResultadoGeneral.setVisible(true);
                menuInformes.setVisible(true);
                menuGestionInformes.setVisible(true);
                menuPlanAccion.setVisible(true);
                menuConsultarPlan.setVisible(true);
                menuEvaluacion.setVisible(true);
            }
            default -> menuGestionCuenta.setVisible(true);
        }
    }

    private void ocultarTodo() {
        menuGestionCuenta.setVisible(false);
        menuGestionUsuario.setVisible(false);
        menuGestionRoles.setVisible(false);
        menuCargarUsuarios.setVisible(false);
        menuResultadoPersonal.setVisible(false);
        menuResultadoGeneral.setVisible(false);
        menuCargarResultados.setVisible(false);
        menuInformes.setVisible(false);
        menuGenerarInforme.setVisible(false);
        menuGestionInformes.setVisible(false);
        menuPlanAccion.setVisible(false);
        menuConsultarPlan.setVisible(false);
        menuEvaluacion.setVisible(false);
    }

    // ── Handlers de navegación ───────────────────────────────────────────────

    @FXML
    private void onGestionCuenta() {
        abrirVentana("/com/saberpro/adapters/views/Cuenta.fxml", "Gestionar cuenta");
    }

    @FXML
    private void onGestionUsuario() {
        abrirVentana("/com/saberpro/adapters/views/Usuario.fxml", "Gestionar usuarios");
    }

    @FXML
    private void onGestionRoles() {
        abrirVentana("/com/saberpro/adapters/views/Rol.fxml", "Gestionar roles");
    }

    @FXML
    private void onCargarUsuarios() {
        abrirVentana("/com/saberpro/adapters/views/CargaUsuario.fxml", "Carga masiva de usuarios");
    }

    @FXML
    private void onResultadoPersonal() {
        abrirVentana("/com/saberpro/adapters/views/ResultadoPersonal.fxml", "Resultado personal");
    }

    @FXML
    private void onResultadoGeneral() {
        abrirVentana("/com/saberpro/adapters/views/ResultadosGenerales.fxml", "Resultados generales");
    }

    @FXML
    private void onCargarResultados() {
        abrirVentana("/com/saberpro/adapters/views/CargaResultados.fxml", "Cargar resultados");
    }

    @FXML
    private void onGenerarInforme() {
        abrirVentana("/com/saberpro/adapters/views/Informe.fxml", "Generar informe con IA");
    }

    @FXML
    private void onGestionInformes() {
        // TODO: implementar cuando esté la vista de gestión de informes
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente",
                "La gestión de informes estará disponible pronto.");
    }

    @FXML
    private void onConsultarPlan() {
        // TODO: implementar cuando esté la vista de plan de acción
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente",
                "El plan de acción estará disponible pronto.");
    }

    @FXML
    private void onEvaluacion() {
        // TODO: implementar cuando esté la vista de evaluación
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente",
                "La evaluación y seguimiento estará disponible pronto.");
    }

    @FXML
    private void onCerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas cerrar la sesión actual?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Cerrar sesión");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                SesionActual.cerrarSesion();
                abrirVentanaPrincipal("/com/saberpro/adapters/views/Login.fxml", "SABER PRO - Login");
                cerrarVentanaActual();
            }
        });
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    /**
     * Abre una ventana secundaria (modal) sin cerrar el menú.
     */
    @FXML private StackPane contenidoCentral;

    private void abrirVentana(String fxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            contenidoCentral.getChildren().setAll(root);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir vista", e.getMessage());
        }
    }

    /**
     * Abre una ventana sin modality (para el login al cerrar sesión).
     */
    private void abrirVentanaPrincipal(String fxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error al abrir ventana", e.getMessage());
        }
    }

    private void cerrarVentanaActual() {
        Stage stage = (Stage) lblBienvenida.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}