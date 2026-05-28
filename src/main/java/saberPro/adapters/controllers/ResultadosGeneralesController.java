package saberPro.adapters.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import saberPro.entities.FiltrosConsulta;
import saberPro.entities.ResultadoSaberPro;
import saberPro.infrastructure.persistence.repositories.ResultadosRepositoryImpl;
import saberPro.infrastructure.services.ExportacionGatewayImpl;
import saberPro.usecases.results.ConsultarResultadosGeneralesUseCase;
import saberPro.usecases.results.ExportarResultadosExcelUseCase;
import saberPro.usecases.results.ExportarResultadosPDFUseCase;

import java.util.List;

public class ResultadosGeneralesController {

    @FXML private TextField        txtAnio;
    @FXML private ComboBox<String> cbxSemestre;
    @FXML private ComboBox<String> cbxPrograma;
    @FXML private ComboBox<String> cbxCiudad;
    @FXML private ComboBox<String> cbxModulo;

    @FXML private TableView<ResultadoSaberPro>           tablaResultados;
    @FXML private TableColumn<ResultadoSaberPro, String>  colNombre;
    @FXML private TableColumn<ResultadoSaberPro, String>  colApellido;
    @FXML private TableColumn<ResultadoSaberPro, String>  colCc;
    @FXML private TableColumn<ResultadoSaberPro, String>  colRegistro;
    @FXML private TableColumn<ResultadoSaberPro, Integer> colAnio;
    @FXML private TableColumn<ResultadoSaberPro, Integer> colSemestre;
    @FXML private TableColumn<ResultadoSaberPro, String>  colPrograma;
    @FXML private TableColumn<ResultadoSaberPro, String>  colCiudad;
    @FXML private TableColumn<ResultadoSaberPro, Double>  colPuntaje;
    @FXML private TableColumn<ResultadoSaberPro, Double>  colPercentil;

    private final ResultadosRepositoryImpl  repository      = new ResultadosRepositoryImpl();
    private final ExportacionGatewayImpl    exportGateway   = new ExportacionGatewayImpl();
    private final ConsultarResultadosGeneralesUseCase consultarUseCase =
            new ConsultarResultadosGeneralesUseCase(repository);
    private final ExportarResultadosPDFUseCase   exportPDFUseCase =
            new ExportarResultadosPDFUseCase(exportGateway);
    private final ExportarResultadosExcelUseCase exportExcelUseCase =
            new ExportarResultadosExcelUseCase(exportGateway);

    // Lista actual para exportar
    private List<ResultadoSaberPro> resultadosActuales;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCc.setCellValueFactory(new PropertyValueFactory<>("cc"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("numeroRegistro"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        colPrograma.setCellValueFactory(new PropertyValueFactory<>("programa"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntajeGlobal"));
        colPercentil.setCellValueFactory(new PropertyValueFactory<>("percentilGlobal"));

        cbxSemestre.setItems(FXCollections.observableArrayList("Todos", "1", "2"));
        cbxSemestre.setValue("Todos");

        // Programas y ciudades se pueden cargar desde BD si se necesita;
        // por ahora se dejan libres para que el usuario escriba en txtAnio
        // y seleccione desde los combos que se poblarán desde el FXML o BD.
    }

    @FXML
    private void onConsultar() {
        FiltrosConsulta filtros = new FiltrosConsulta();

        String anioTexto = txtAnio.getText().trim();
        if (!anioTexto.isEmpty()) {
            try {
                filtros.setAnio(Integer.parseInt(anioTexto));
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Año inválido",
                        "El año debe ser un número.");
                return;
            }
        }

        String semestre = cbxSemestre.getValue();
        if (semestre != null && !semestre.equals("Todos")) {
            filtros.setSemestre(Integer.parseInt(semestre));
        }

        String programa = cbxPrograma.getValue();
        if (programa != null && !programa.equals("Todos")) {
            filtros.setPrograma(programa);
        }

        String ciudad = cbxCiudad.getValue();
        if (ciudad != null && !ciudad.equals("Todos")) {
            filtros.setCiudad(ciudad);
        }

        String modulo = cbxModulo.getValue();
        if (modulo != null && !modulo.equals("Todos")) {
            filtros.setModulo(modulo);
        }

        try {
            resultadosActuales = consultarUseCase.ejecutar(filtros);
            tablaResultados.setItems(FXCollections.observableArrayList(resultadosActuales));
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin resultados", e.getMessage());
            tablaResultados.getItems().clear();
            resultadosActuales = null;
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onExportarPDF() {
        if (resultadosActuales == null || resultadosActuales.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin datos",
                    "Primero realice una consulta.");
            return;
        }

        String ruta = elegirRuta("PDF", "*.pdf");
        if (ruta == null) return;

        try {
            exportPDFUseCase.ejecutar(resultadosActuales, ruta);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "PDF exportado correctamente en:\n" + ruta);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar", e.getMessage());
        }
    }

    @FXML
    private void onExportarExcel() {
        if (resultadosActuales == null || resultadosActuales.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin datos",
                    "Primero realice una consulta.");
            return;
        }

        String ruta = elegirRuta("Excel", "*.xlsx");
        if (ruta == null) return;

        try {
            exportExcelUseCase.ejecutar(resultadosActuales, ruta);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Excel exportado correctamente en:\n" + ruta);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar", e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        txtAnio.clear();
        cbxSemestre.setValue("Todos");
        cbxPrograma.setValue(null);
        cbxCiudad.setValue(null);
        cbxModulo.setValue(null);
        tablaResultados.getItems().clear();
        resultadosActuales = null;
    }

    private String elegirRuta(String descripcion, String extension) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar como " + descripcion);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(descripcion, extension));
        Stage stage = (Stage) tablaResultados.getScene().getWindow();
        java.io.File archivo = chooser.showSaveDialog(stage);
        return archivo != null ? archivo.getAbsolutePath() : null;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}