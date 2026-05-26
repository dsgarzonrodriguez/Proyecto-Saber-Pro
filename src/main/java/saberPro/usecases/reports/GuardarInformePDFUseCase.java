package saberPro.usecases.reports;

import saberPro.infrastructure.gateways.InformeGateway;

public class GuardarInformePDFUseCase {

    private final InformeGateway informeGateway;

    public GuardarInformePDFUseCase(InformeGateway informeGateway) {
        this.informeGateway = informeGateway;
    }

    public void ejecutar(String textoInforme) {
        if (textoInforme == null || textoInforme.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay informe generado. Genere el informe antes de guardarlo.");
        }
        // La conversión a PDF la maneja la implementación concreta de InformeGateway
        // (o directamente la vista con iText, según el diseño que elijas).
        // Este use case actúa como guardián de precondiciones.
    }
}