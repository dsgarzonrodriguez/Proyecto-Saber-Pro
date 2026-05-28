package saberPro.adapters.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import saberPro.entities.ModuloSaber;
import saberPro.entities.ResultadoSaberPro;
import saberPro.infrastructure.persistence.repositories.ResultadosRepositoryImpl;
import saberPro.usecases.results.BuscarEstudianteUseCase;
import saberPro.usecases.results.ConsultarResultadoPersonalUseCase;

import java.util.List;

public class ResultadoPersonalController {

    // --- Panel búsqueda de estudiante ---
    @FXML private TextField txtNombre;
    @FXML private TextField txtCc;
    @FXML private TableView<ResultadoSaberPro>       tablaEstudiantes;
    @FXML private TableColumn<ResultadoSaberPro, String>  colNombre;
    @FXML private TableColumn<ResultadoSaberPro, String>  colApellido;
    @FXML private TableColumn<ResultadoSaberPro, String>  colCc;
    @FXML private TableColumn<ResultadoSaberPro, String>  colRegistro;
    @FXML private TableColumn<ResultadoSaberPro, Integer> colAnio;
    @FXML private TableColumn<ResultadoSaberPro, Integer> colSemestre;

    // --- Panel resultado personal ---
    @FXML private Label lblPuntajeGlobal;
    @FXML private Label lblPercentilGlobal;
    @FXML private Label lblPrograma;
    @FXML private Label lblCiudad;
    @FXML private TableView<ModuloSaber>       tablaModulos;
    @FXML private TableColumn<ModuloSaber, String> colModulo;
    @FXML private TableColumn<ModuloSaber, Double> colPuntajeModulo;
    @FXML private TableColumn<ModuloSaber, Double> colPercentilModulo;

    private final ResultadosRepositoryImpl repository = new ResultadosRepositoryImpl();
    private final BuscarEstudianteUseCase buscarUseCase =
            new BuscarEstudianteUseCase(repository);
    private final ConsultarResultadoPersonalUseCase consultarUseCase =
            new ConsultarResultadoPersonalUseCase(repository);

    // Guarda el resultado actual para reutilizarlo
    private ResultadoSaberPro resultadoActual;

    @FXML
    public void initialize() {
        // Tabla estudiantes
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCc.setCellValueFactory(new PropertyValueFactory<>("cc"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("numeroRegistro"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));

        // Tabla módulos
        colModulo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuntajeModulo.setCellValueFactory(new PropertyValueFactory<>("puntaje"));
        colPercentilModulo.setCellValueFactory(new PropertyValueFactory<>("percentilNacional"));

        limpiarResultado();
    }

    @FXML
    private void onBuscarEstudiante() {
        String nombre = txtNombre.getText().trim();
        String cc     = txtCc.getText().trim();

        try {
            List<ResultadoSaberPro> lista = buscarUseCase.ejecutar(nombre, cc);
            tablaEstudiantes.setItems(FXCollections.observableArrayList(lista));
            limpiarResultado();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin resultados", e.getMessage());
            tablaEstudiantes.getItems().clear();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onSeleccionarEstudiante() {
        ResultadoSaberPro seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        try {
            resultadoActual = consultarUseCase.ejecutar(
                    seleccionado.getCc(),
                    seleccionado.getNumeroRegistro(),
                    seleccionado.getAnio(),
                    seleccionado.getSemestre());

            poblarResultado(resultadoActual);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin resultado", e.getMessage());
            limpiarResultado();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        txtNombre.clear();
        txtCc.clear();
        tablaEstudiantes.getItems().clear();
        limpiarResultado();
        resultadoActual = null;
    }

    private void poblarResultado(ResultadoSaberPro r) {
        lblPuntajeGlobal.setText(r.getPuntajeGlobal() != null
                ? String.valueOf(r.getPuntajeGlobal()) : "N/A");
        lblPercentilGlobal.setText(r.getPercentilGlobal() != null
                ? String.valueOf(r.getPercentilGlobal()) : "N/A");
        lblPrograma.setText(r.getPrograma() != null ? r.getPrograma() : "N/A");
        lblCiudad.setText(r.getCiudad() != null ? r.getCiudad() : "N/A");

        if (r.getModulos() != null) {
            tablaModulos.setItems(FXCollections.observableArrayList(r.getModulos()));
        }
    }

    private void limpiarResultado() {
        lblPuntajeGlobal.setText("-");
        lblPercentilGlobal.setText("-");
        lblPrograma.setText("-");
        lblCiudad.setText("-");
        tablaModulos.getItems().clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}