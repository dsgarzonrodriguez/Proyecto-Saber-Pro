package saberPro.adapters.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import saberPro.entities.Usuario;
import saberPro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import saberPro.usecases.user.LoginUsuarioUseCase;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;

    private final LoginUsuarioUseCase loginUseCase =
            new LoginUsuarioUseCase(new UsuarioRepositoryImpl());

    @FXML
    private void onIniciarSesion() {
        String correo     = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Por favor ingrese su correo y contraseña.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);

        try {
            Usuario logueado = loginUseCase.ejecutar(usuario);
            SesionActual.setUsuario(logueado);
            abrirVentana("/saberPro/adapters/views/Menu.fxml", "Menú principal");
            cerrarVentanaActual();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onRecuperar() {
        abrirVentana("/saberPro/adapters/views/Recuperar.fxml", "Recuperar contraseña");
        cerrarVentanaActual();
    }

    private void abrirVentana(String fxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // <-- agrega esto
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir ventana", 
                e.getClass().getSimpleName() + ": " + e.getMessage() 
                + (e.getCause() != null ? "\nCausa: " + e.getCause().getMessage() : ""));
        }
    }

    private void cerrarVentanaActual() {
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
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