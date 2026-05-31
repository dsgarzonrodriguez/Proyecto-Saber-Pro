package com.saberpro.adapters.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.saberpro.entities.Roles;
import com.saberpro.entities.Usuario;
import com.saberpro.infrastructure.persistence.repositories.RolesRepositoryImpl;
import com.saberpro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import com.saberpro.usecases.user.*;

import java.util.List;

public class UsuarioController {

    @FXML private TextField     txtId;
    @FXML private TextField     txtNombre;
    @FXML private TextField     txtApellido;
    @FXML private TextField     txtTelefono;
    @FXML private TextField     txtCC;
    @FXML private TextField     txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<Roles> cbxRol;
    @FXML private Button        btnEliminar;
    @FXML private Button        btnActivar;

    private final UsuarioRepositoryImpl repository    = new UsuarioRepositoryImpl();
    private final BuscarUsuarioUseCase  buscarUseCase = new BuscarUsuarioUseCase(repository);
    private final RegistrarUsuarioUseCase registrarUseCase = new RegistrarUsuarioUseCase(repository);
    private final ModificarUsuarioUseCase modificarUseCase = new ModificarUsuarioUseCase(repository);
    private final InhabilitarUsuarioUseCase inhabilitarUseCase = new InhabilitarUsuarioUseCase(repository);
    private final HabilitarUsuarioUseCase   habilitarUseCase   = new HabilitarUsuarioUseCase(repository);

    @FXML
    public void initialize() {
        txtId.setVisible(false);
        btnActivar.setVisible(false);
        cargarRoles();
    }

    private void cargarRoles() {
        try {
            List<Roles> roles = new RolesRepositoryImpl().listarTodos();
            cbxRol.setItems(FXCollections.observableArrayList(roles));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los roles.");
        }
    }

    @FXML
    private void onBuscar() {
        String idTexto = txtId.getText().trim();
        String correo  = txtCorreo.getText().trim();

        if (idTexto.isEmpty() && correo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Ingrese un ID o correo para buscar.");
            return;
        }

        Usuario usuario = new Usuario();
        try {
            if (!idTexto.isEmpty()) usuario.setId_usuario(Integer.parseInt(idTexto));
            else usuario.setCorreo(correo);

            Usuario encontrado = buscarUseCase.ejecutar(usuario);
            poblarCampos(encontrado);

            if (encontrado.isHabilitado()) {
                btnEliminar.setVisible(true);
                btnActivar.setVisible(false);
            } else {
                btnEliminar.setVisible(false);
                btnActivar.setVisible(true);
            }
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "No encontrado", e.getMessage());
            limpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onRegistrar() {
        try {
            Usuario usuario = obtenerUsuarioDeCampos();
            registrarUseCase.ejecutar(usuario);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Usuario registrado correctamente.");
            limpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onModificar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin ID",
                    "Primero busque el usuario a modificar.");
            return;
        }
        try {
            int idOriginal = Integer.parseInt(idTexto);
            Usuario usuario = obtenerUsuarioDeCampos();
            modificarUseCase.ejecutar(usuario, idOriginal);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Usuario modificado correctamente.");
            limpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onInhabilitar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin ID",
                    "Primero busque el usuario a inhabilitar.");
            return;
        }
        try {
            inhabilitarUseCase.ejecutar(Integer.parseInt(idTexto));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Usuario inhabilitado correctamente.");
            limpiar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onHabilitar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin ID",
                    "Primero busque el usuario a habilitar.");
            return;
        }
        try {
            habilitarUseCase.ejecutar(Integer.parseInt(idTexto));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Usuario habilitado correctamente.");
            btnActivar.setVisible(false);
            btnEliminar.setVisible(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiar();
    }

    private void poblarCampos(Usuario u) {
        txtId.setText(String.valueOf(u.getId_usuario()));
        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        txtTelefono.setText(u.getTelefono());
        txtCC.setText(u.getCc());
        txtCorreo.setText(u.getCorreo());
        txtContrasena.setText(u.getContrasena());

        for (Roles r : cbxRol.getItems()) {
            if (r.getId_roles() == u.getRol().getId_roles()) {
                cbxRol.setValue(r);
                break;
            }
        }
    }

    private Usuario obtenerUsuarioDeCampos() {
        Usuario u = new Usuario();
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setTelefono(txtTelefono.getText().trim());
        u.setCc(txtCC.getText().trim());
        u.setCorreo(txtCorreo.getText().trim());
        u.setContrasena(txtContrasena.getText());
        u.setRol(cbxRol.getValue());
        return u;
    }

    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtCC.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        cbxRol.getSelectionModel().clearSelection();
        btnEliminar.setVisible(true);
        btnActivar.setVisible(false);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}