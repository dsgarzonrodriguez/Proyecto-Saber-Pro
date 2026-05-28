package saberPro.infrastructure.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import saberPro.entities.ResultadoSaberPro;
import saberPro.infrastructure.gateways.ExportacionGateway;

import java.io.FileOutputStream;
import java.util.List;

public class ExportacionGatewayImpl implements ExportacionGateway {

    private static final String[] COLUMNAS = {
        "Nombre", "Apellido", "CC", "Registro",
        "Año", "Semestre", "Programa", "Ciudad",
        "Puntaje Global", "Percentil Global"
    };

    @Override
    public void exportarPDF(List<ResultadoSaberPro> resultados, String rutaDestino)
            throws Exception {

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(rutaDestino));
        doc.open();

        // Título
        com.itextpdf.text.Font fuenteTitulo = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 14,
                com.itextpdf.text.Font.BOLD);
        Paragraph titulo = new Paragraph("Reporte de Resultados Saber Pro", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        doc.add(titulo);

        // Tabla
        PdfPTable tabla = new PdfPTable(COLUMNAS.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{2f, 2f, 1.5f, 1.5f, 1f, 1f, 2.5f, 1.5f, 1.5f, 1.5f});

        // Encabezados
        com.itextpdf.text.Font fuenteHeader = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 9,
                com.itextpdf.text.Font.BOLD, BaseColor.WHITE);

        for (String col : COLUMNAS) {
            PdfPCell cell = new PdfPCell(new Phrase(col, fuenteHeader));
            cell.setBackgroundColor(new BaseColor(0, 0, 153));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            tabla.addCell(cell);
        }

        // Filas
        com.itextpdf.text.Font fuenteFila = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 8);

        for (ResultadoSaberPro r : resultados) {
            tabla.addCell(new Phrase(nvl(r.getNombre()),        fuenteFila));
            tabla.addCell(new Phrase(nvl(r.getApellido()),      fuenteFila));
            tabla.addCell(new Phrase(nvl(r.getCc()),            fuenteFila));
            tabla.addCell(new Phrase(nvl(r.getNumeroRegistro()),fuenteFila));
            tabla.addCell(new Phrase(String.valueOf(r.getAnio()),     fuenteFila));
            tabla.addCell(new Phrase(String.valueOf(r.getSemestre()), fuenteFila));
            tabla.addCell(new Phrase(nvl(r.getPrograma()),      fuenteFila));
            tabla.addCell(new Phrase(nvl(r.getCiudad()),        fuenteFila));
            tabla.addCell(new Phrase(r.getPuntajeGlobal()  != null
                    ? String.valueOf(r.getPuntajeGlobal())  : "N/A", fuenteFila));
            tabla.addCell(new Phrase(r.getPercentilGlobal() != null
                    ? String.valueOf(r.getPercentilGlobal()) : "N/A", fuenteFila));
        }

        doc.add(tabla);
        doc.close();
    }

    @Override
    public void exportarExcel(List<ResultadoSaberPro> resultados, String rutaDestino)
            throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resultados");

        // Estilo encabezado
        CellStyle estiloHeader = workbook.createCellStyle();
        Font fuenteHeader = workbook.createFont();
        fuenteHeader.setBold(true);
        fuenteHeader.setColor(IndexedColors.WHITE.getIndex());
        estiloHeader.setFont(fuenteHeader);
        estiloHeader.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloHeader.setAlignment(HorizontalAlignment.CENTER);

        // Fila de encabezados
        Row header = sheet.createRow(0);
        for (int i = 0; i < COLUMNAS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(COLUMNAS[i]);
            cell.setCellStyle(estiloHeader);
        }

        // Filas de datos
        int rowIndex = 1;
        for (ResultadoSaberPro r : resultados) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(nvl(r.getNombre()));
            row.createCell(1).setCellValue(nvl(r.getApellido()));
            row.createCell(2).setCellValue(nvl(r.getCc()));
            row.createCell(3).setCellValue(nvl(r.getNumeroRegistro()));
            row.createCell(4).setCellValue(r.getAnio());
            row.createCell(5).setCellValue(r.getSemestre());
            row.createCell(6).setCellValue(nvl(r.getPrograma()));
            row.createCell(7).setCellValue(nvl(r.getCiudad()));
            row.createCell(8).setCellValue(r.getPuntajeGlobal()  != null
                    ? r.getPuntajeGlobal()  : 0);
            row.createCell(9).setCellValue(r.getPercentilGlobal() != null
                    ? r.getPercentilGlobal() : 0);
        }

        // Autoajustar columnas
        for (int i = 0; i < COLUMNAS.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream out = new FileOutputStream(rutaDestino)) {
            workbook.write(out);
        }

        workbook.close();
    }

    private String nvl(String valor) {
        return valor != null ? valor : "";
    }
}