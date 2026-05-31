package com.saberpro.adapters.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.saberpro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import com.saberpro.usecases.user.RecuperarContrasenaUseCase;

public class VerificarController {

    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtRepetirContrasena;

    private final RecuperarContrasenaUseCase recuperarUseCase =
            new RecuperarContrasenaUseCase(new UsuarioRepositoryImpl());

    private String correo;
    private String codigoEsperado;

    public void iniciar(String correo, String codigoEsperado) {
        this.correo         = correo;
        this.codigoEsperado = codigoEsperado;
    }

    @FXML
    private void onVerificarYCambiar() {
        String codigoIngresado  = txtCodigo.getText().trim();
        String nuevaContrasena  = txtNuevaContrasena.getText();
        String repetirContrasena = txtRepetirContrasena.getText();

        if (codigoIngresado.isEmpty() || nuevaContrasena.isEmpty() || repetirContrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Por favor complete todos los campos.");
            return;
        }

        if (!codigoIngresado.equals(codigoEsperado)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Código incorrecto",
                    "El código ingresado no coincide con el enviado.");
            return;
        }

        try {
            recuperarUseCase.ejecutar(correo, nuevaContrasena, repetirContrasena);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Contraseña actualizada",
                    "Su contraseña ha sido cambiada correctamente.");
            abrirLogin();
            cerrarVentanaActual();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void abrirLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Iniciar sesión");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir login", e.getMessage());
        }
    }

    private void cerrarVentanaActual() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
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