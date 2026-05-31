package com.saberpro.usecases.results;

import com.saberpro.entities.ResultadoSaberPro;
import com.saberpro.infrastructure.gateways.ExportacionGateway;

import java.util.List;

public class ExportarResultadosPDFUseCase {

    private final ExportacionGateway exportacionGateway;

    public ExportarResultadosPDFUseCase(ExportacionGateway exportacionGateway) {
        this.exportacionGateway = exportacionGateway;
    }

    public void ejecutar(List<ResultadoSaberPro> resultados, String rutaDestino) throws Exception {
        if (resultados == null || resultados.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay datos para exportar.");
        }
        if (rutaDestino == null || rutaDestino.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe especificar una ruta de destino.");
        }

        exportacionGateway.exportarPDF(resultados, rutaDestino);
    }
}