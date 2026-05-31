package com.saberpro.adapters.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.saberpro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import com.saberpro.infrastructure.services.CorreoGatewayImpl;
import com.saberpro.usecases.user.EnviarCodigoRecuperacionUseCase;

public class RecuperarController {

    @FXML private TextField txtCorreo;
    @FXML private Button btnEnviar;

    private final EnviarCodigoRecuperacionUseCase enviarCodigoUseCase =
            new EnviarCodigoRecuperacionUseCase(
                    new UsuarioRepositoryImpl(),
                    new CorreoGatewayImpl());

    private String codigoGenerado;

    @FXML
    private void onEnviarCodigo() {
        String correo = txtCorreo.getText().trim();

        if (correo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                    "Por favor ingrese su correo.");
            return;
        }

        try {
            btnEnviar.setDisable(true);
            codigoGenerado = enviarCodigoUseCase.ejecutar(correo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Código enviado",
                    "Se ha enviado un código de verificación a su correo.");
            abrirVerificar(correo, codigoGenerado);
            cerrarVentanaActual();
        } catch (IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Espere", e.getMessage());
            btnEnviar.setDisable(false);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
            btnEnviar.setDisable(false);
        }
    }

    private void abrirVerificar(String correo, String codigo) {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/Verificar.fxml"));
            javafx.scene.Parent root = loader.load();

            VerificarController ctrl = loader.getController();
            ctrl.iniciar(correo, codigo);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Verificar código");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir verificación", e.getMessage());
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