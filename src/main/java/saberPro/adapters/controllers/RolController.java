package saberPro.adapters.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import saberPro.entities.Roles;
import saberPro.infrastructure.persistence.repositories.RolesRepositoryImpl;
import saberPro.usecases.rol.BuscarRolUseCase;
import saberPro.usecases.rol.EditarRolUseCase;
import saberPro.usecases.rol.EliminarRolUseCase;
import saberPro.usecases.rol.RegistrarRolUseCase;

import java.util.List;

public class RolController {

    @FXML private TextField              txtId;
    @FXML private TextField              txtNombre;
    @FXML private TableView<Roles>       tablaRoles;
    @FXML private TableColumn<Roles, Integer> colId;
    @FXML private TableColumn<Roles, String>  colNombre;

    private final RolesRepositoryImpl repository     = new RolesRepositoryImpl();
    private final RegistrarRolUseCase registrarUseCase = new RegistrarRolUseCase(repository);
    private final EditarRolUseCase    editarUseCase    = new EditarRolUseCase(repository);
    private final EliminarRolUseCase  eliminarUseCase  = new EliminarRolUseCase(repository);
    private final BuscarRolUseCase    buscarUseCase    = new BuscarRolUseCase(repository);

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id_roles"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        txtId.setEditable(false);
        cargarTabla();
    }

    @FXML
    private void onBuscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                    "Ingrese un ID para buscar.");
            return;
        }
        try {
            Roles rol = buscarUseCase.ejecutar(Integer.parseInt(idTexto));
            txtNombre.setText(rol.getNombre());
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "No encontrado", e.getMessage());
            limpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onRegistrar() {
        String nombre = txtNombre.getText().trim();
        try {
            Roles rol = new Roles();
            rol.setNombre(nombre);
            registrarUseCase.ejecutar(rol);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Rol registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin ID",
                    "Primero busque o seleccione el rol a editar.");
            return;
        }
        try {
            int idOriginal = Integer.parseInt(idTexto);
            Roles rol = new Roles();
            rol.setId_roles(idOriginal);
            rol.setNombre(txtNombre.getText().trim());
            editarUseCase.ejecutar(rol, idOriginal);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Rol editado correctamente.");
            limpiar();
            cargarTabla();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin ID",
                    "Primero busque o seleccione el rol a eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de que desea eliminar este rol?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    eliminarUseCase.ejecutar(Integer.parseInt(idTexto));
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "Rol eliminado correctamente.");
                    limpiar();
                    cargarTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onSeleccionarFila() {
        Roles seleccionado = tablaRoles.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtId.setText(String.valueOf(seleccionado.getId_roles()));
            txtNombre.setText(seleccionado.getNombre());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiar();
    }

    private void cargarTabla() {
        try {
            List<Roles> lista = repository.listarTodos();
            tablaRoles.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los roles.");
        }
    }

    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        tablaRoles.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}