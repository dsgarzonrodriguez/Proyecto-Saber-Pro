package com.saberpro.adapters.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.saberpro.entities.Roles;
import com.saberpro.entities.Usuario;
import com.saberpro.infrastructure.persistence.repositories.RolesRepositoryImpl;
import com.saberpro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import com.saberpro.infrastructure.persistence.repositories.UsuarioRepositoryImpl;
import com.saberpro.usecases.user.CargaMasivaUsuarioUseCase;
import com.saberpro.usecases.user.CargaMasivaUsuarioUseCase.ResultadoCarga;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CargaUsuarioController {

    // ── FXML ────────────────────────────────────────────────────────────────
    @FXML private Label                          lblArchivo;
    @FXML private Button                         btnSeleccionar;
    @FXML private Button                         btnCargar;
    @FXML private Label                          lblResumen;

    @FXML private TableView<Usuario>             tablaPrevia;
    @FXML private TableColumn<Usuario, String>   colNombre;
    @FXML private TableColumn<Usuario, String>   colApellido;
    @FXML private TableColumn<Usuario, String>   colTelefono;
    @FXML private TableColumn<Usuario, String>   colCC;
    @FXML private TableColumn<Usuario, String>   colCorreo;
    @FXML private TableColumn<Usuario, String>   colRol;

    @FXML private TableView<String>              tablaErrores;
    @FXML private TableColumn<String, String>    colError;

    // ── Estado interno ──────────────────────────────────────────────────────
    private File          archivoSeleccionado;
    private List<Usuario> usuariosParsed = new ArrayList<>();

    private final RolesRepositoryImpl rolesRepo   = new RolesRepositoryImpl();
    private final CargaMasivaUsuarioUseCase cargaUseCase = new CargaMasivaUsuarioUseCase(new UsuarioRepositoryImpl());

    // ── Inicialización ──────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCC.setCellValueFactory(new PropertyValueFactory<>("cc"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        colRol.setCellValueFactory(data -> {
            Roles r = data.getValue().getRol();
            return new javafx.beans.property.SimpleStringProperty(
                    r != null ? r.getNombre() : "");
        });

        colError.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue()));

        btnCargar.setDisable(true);
        lblResumen.setText("");
    }

    // ── Handlers ────────────────────────────────────────────────────────────

    @FXML
    private void onSeleccionarArchivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar archivo Excel");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));

        Stage stage = (Stage) btnSeleccionar.getScene().getWindow();
        File archivo = chooser.showOpenDialog(stage);
        if (archivo == null) return;

        archivoSeleccionado = archivo;
        lblArchivo.setText(archivo.getName());
        lblResumen.setText("");
        tablaErrores.getItems().clear();

        try {
            usuariosParsed = parsearExcel(archivo);
            tablaPrevia.setItems(FXCollections.observableArrayList(usuariosParsed));
            btnCargar.setDisable(usuariosParsed.isEmpty());

            if (usuariosParsed.isEmpty()) {
                lblResumen.setText("⚠ El archivo no contiene filas de datos.");
            } else {
                lblResumen.setText("Vista previa: " + usuariosParsed.size() +
                        " usuario(s) encontrado(s). Revise y presione 'Cargar'.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al leer el archivo", e.getMessage());
        }
    }

    @FXML
    private void onCargar() {
        if (usuariosParsed.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin datos", "No hay usuarios para cargar.");
            return;
        }

        ResultadoCarga resultado = cargaUseCase.ejecutar(usuariosParsed);

        tablaErrores.setItems(FXCollections.observableArrayList(resultado.getErrores()));

        String resumen = "✔ " + resultado.getExitosos() + " usuario(s) cargado(s)";
        if (resultado.hayErrores())
            resumen += "   |   ✖ " + resultado.getFallidos() + " con error";
        lblResumen.setText(resumen);

        if (!resultado.hayErrores()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Carga completada",
                    "Se cargaron " + resultado.getExitosos() + " usuarios correctamente.");
            limpiar();
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Carga con errores",
                    resultado.getExitosos() + " usuario(s) cargado(s) correctamente.\n" +
                    resultado.getFallidos() + " fila(s) con error. Revise la tabla de errores.");
        }
    }

    @FXML
    private void onLimpiar() {
        limpiar();
    }

    // ── Parseo del Excel ─────────────────────────────────────────────────────

    private List<Usuario> parsearExcel(File archivo) throws Exception {
        List<Usuario> lista = new ArrayList<>();
        List<Roles>   roles = rolesRepo.listarTodos();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet hoja = wb.getSheetAt(0);
            int ultimaFila = hoja.getLastRowNum();

            for (int i = 1; i <= ultimaFila; i++) {
                Row fila = hoja.getRow(i);
                if (fila == null || esFilaVacia(fila)) continue;

                Usuario u = new Usuario();
                u.setNombre(celda(fila, 0));
                u.setApellido(celda(fila, 1));
                u.setTelefono(celda(fila, 2));
                u.setCc(celda(fila, 3));
                u.setCorreo(celda(fila, 4));
                u.setContrasena(celda(fila, 5));

                String nombreRol    = celda(fila, 6);
                Roles  rolEncontrado = roles.stream()
                        .filter(r -> r.getNombre().equalsIgnoreCase(nombreRol.trim()))
                        .findFirst()
                        .orElse(null);

                if (rolEncontrado == null) {
                    Roles rolDesconocido = new Roles();
                    rolDesconocido.setNombre(nombreRol);
                    u.setRol(rolDesconocido);
                } else {
                    u.setRol(rolEncontrado);
                }

                lista.add(u);
            }
        }
        return lista;
    }

    private String celda(Row fila, int col) {
        org.apache.poi.ss.usermodel.Cell c =
                fila.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return "";

        CellType tipo = c.getCellType();

        if (tipo == CellType.STRING) {
            return c.getStringCellValue().trim();
        } else if (tipo == CellType.NUMERIC) {
            return String.valueOf((long) c.getNumericCellValue());
        } else if (tipo == CellType.BOOLEAN) {
            return String.valueOf(c.getBooleanCellValue());
        } else {
            return "";
        }
    }

    private boolean esFilaVacia(Row fila) {
        for (int c = 0; c <= 6; c++) {
            if (!celda(fila, c).isEmpty()) return false;
        }
        return true;
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private void limpiar() {
        archivoSeleccionado = null;
        usuariosParsed.clear();
        tablaPrevia.getItems().clear();
        tablaErrores.getItems().clear();
        lblArchivo.setText("Ningún archivo seleccionado");
        lblResumen.setText("");
        btnCargar.setDisable(true);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}