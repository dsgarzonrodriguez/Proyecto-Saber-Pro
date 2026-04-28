package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.Conexion;
import com.mycompany.saberpro.Modelo.Usuario;
import com.mycompany.saberpro.Vista.InterResultadosGenerales;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

// Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

// JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class CtrlResultadosGenerales {

    private final InterResultadosGenerales v;
    private final DecimalFormat DF1 = new DecimalFormat("#,##0.0");
    private final DecimalFormat DF2 = new DecimalFormat("#,##0.00");

    // valores de la última consulta (para el PDF)
    private long ultimoN = 0;
    private double ultimoMedia = 0;
    private double ultimoVar = 0;
    private double ultimoCv = 0;
    private final Map<String, ModStats> statsUltimaConsulta = new HashMap<>();

    public CtrlResultadosGenerales(InterResultadosGenerales vista) {
        this.v = vista;

        if (v.getBtnBuscar() != null) {
            v.getBtnBuscar().addActionListener(e -> cargar());
        }

        if (v.getBtnLimpiar() != null) {
            v.getBtnLimpiar().addActionListener(e -> {
                v.limpiarFiltros();
                limpiarTodo();
            });
        }

        if (v.getBtnExportar() != null) {
            v.getBtnExportar().addActionListener(e -> exportarReporte());
        }
    }

    private static class ModStats {

        long n;
        double media;
        double varianza;
        double cv;

        ModStats(long n, double media, double varianza, double cv) {
            this.n = n;
            this.media = media;
            this.varianza = varianza;
            this.cv = cv;
        }
    }

    private void cargar() {

        final JButton b = v.getBtnBuscar();
        final JButton l = v.getBtnLimpiar();

        if (b != null) {
            b.setEnabled(false);
        }
        if (l != null) {
            l.setEnabled(false);
        }

        final Integer anio = v.getAnio();
        final Integer semestre = v.getSemestre();
        final String programa = v.getProgramaTexto();
        final String ciudad = v.getCiudadTexto();

        final StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        final List<Object> params = new ArrayList<Object>();

        if (anio != null) {
            where.append(" AND ano=? ");
            params.add(anio);
        }
        if (semestre != null) {
            where.append(" AND semestre=? ");
            params.add(semestre);
        }
        if (programa != null) {
            where.append(" AND programa=? ");
            params.add(programa);
        }
        if (ciudad != null) {
            where.append(" AND ciudad=? ");
            params.add(ciudad);
        }

        new SwingWorker<Void, Void>() {

            DefaultTableModel model;
            long n;
            double media, var, sd, cv;
            Map<String, ModStats> statsPorModulo = new HashMap<>();

            @Override
            protected Void doInBackground() throws Exception {

                model = new DefaultTableModel(
                        new Object[]{"Año", "Semestre", "Nombre", "Apellido",
                            "CC", "Número de registro", "Programa",
                            "Puntaje global", "Percentil global", "Ciudad"}, 0
                ) {
                    @Override
                    public boolean isCellEditable(int row, int col) {
                        return false;
                    }
                };

                try (Connection con = new Conexion().getConexion()) {

                    String sqlTabla
                            = "SELECT ano, semestre, nombre, apellido, cc, numero_registro, "
                            + "programa, puntaje_global, percentil_global, ciudad "
                            + "FROM vista_resultados_detalle "
                            + where + " ORDER BY apellido, nombre";

                    PreparedStatement ps = con.prepareStatement(sqlTabla);
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("ano"),
                            rs.getInt("semestre"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("cc"),
                            rs.getString("numero_registro"),
                            rs.getString("programa"),
                            getDouble(rs, "puntaje_global"),
                            getDouble(rs, "percentil_global"),
                            rs.getString("ciudad")
                        });
                    }

                    String sqlKpi
                            = "SELECT COUNT(x) AS n, COALESCE(AVG(x),0) AS media, "
                            + "COALESCE(VAR_SAMP(x),0) AS varianza, "
                            + "COALESCE(STDDEV_SAMP(x),0) AS sd "
                            + "FROM (SELECT puntaje_global AS x "
                            + "FROM vista_resultados_detalle "
                            + where
                            + " AND puntaje_global IS NOT NULL AND puntaje_global > 0) t";

                    ps = con.prepareStatement(sqlKpi);
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }

                    rs = ps.executeQuery();
                    if (rs.next()) {
                        n = rs.getLong("n");
                        media = rs.getDouble("media");
                        var = rs.getDouble("varianza");
                        sd = rs.getDouble("sd");
                    }

                    String sqlKpiMod
                            = "SELECT modulo, COUNT(*) AS n, "
                            + "COALESCE(AVG(puntaje_modulo),0) AS media, "
                            + "COALESCE(VAR_SAMP(puntaje_modulo),0) AS varianza, "
                            + "COALESCE(STDDEV_SAMP(puntaje_modulo),0) AS sd "
                            + "FROM vista_resultados_modulo_detalle "
                            + where
                            + " AND puntaje_modulo IS NOT NULL AND puntaje_modulo > 0 "
                            + "GROUP BY modulo";

                    ps = con.prepareStatement(sqlKpiMod);
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }

                    rs = ps.executeQuery();
                    while (rs.next()) {

                        String modulo = rs.getString("modulo");
                        long nM = rs.getLong("n");
                        double mediaM = rs.getDouble("media");
                        double varM = rs.getDouble("varianza");
                        double sdM = rs.getDouble("sd");
                        double cvM = mediaM != 0 ? (sdM / mediaM) * 100 : 0;

                        statsPorModulo.put(modulo.trim().toUpperCase(),
                                new ModStats(nM, mediaM, varM, cvM));
                    }
                }

                cv = media != 0 ? (sd / media) * 100 : 0;
                return null;
            }

            @Override
            protected void done() {
                try {
                    v.getTabla().setModel(model);
                    v.setTotal(n);
                    v.setPromedio(DF1.format(media));
                    v.setVarianza(DF2.format(var));
                    v.setCv(DF1.format(cv) + " %");

                    // guardar para el PDF
                    ultimoN = n;
                    ultimoMedia = media;
                    ultimoVar = var;
                    ultimoCv = cv;
                    statsUltimaConsulta.clear();
                    statsUltimaConsulta.putAll(statsPorModulo);

                    v.limpiarIndicadoresModulos();

                    String[] modulos = {
                        "COMUNICACIÓN ESCRITA",
                        "RAZONAMIENTO CUANTITATIVO",
                        "LECTURA CRÍTICA",
                        "COMPETENCIAS CIUDADANAS",
                        "INGLÉS"
                    };

                    for (int i = 0; i < modulos.length; i++) {
                        ModStats st = statsPorModulo.get(modulos[i]);
                        if (st != null && st.n > 0) {
                            v.setIndicadoresModulo(i,
                                    DF1.format(st.media),
                                    DF2.format(st.varianza),
                                    DF1.format(st.cv) + " %");
                            v.setNModulo(i, String.valueOf(st.n));
                        }
                    }

                    actualizarGraficas(statsPorModulo);

                    if (n == 0) {
                        JOptionPane.showMessageDialog(v,
                                "No hay datos para los filtros seleccionados.",
                                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(v, "Error: " + ex.getMessage());
                } finally {
                    if (b != null) {
                        b.setEnabled(true);
                    }
                    if (l != null) {
                        l.setEnabled(true);
                    }
                }
            }

        }.execute();
    }

    private Double getDouble(ResultSet rs, String col) throws Exception {
        BigDecimal bd = rs.getBigDecimal(col);
        return (bd != null) ? bd.doubleValue() : null;
    }

    private void limpiarTodo() {
        v.getTabla().setModel(new DefaultTableModel());
        v.limpiarIndicadoresModulos();
        v.setTotal(0);
        v.setPromedio("");
        v.setVarianza("");
        v.setCv("");

        v.getPanel1().removeAll();
        v.getPanel3().removeAll();
        v.getPanel1().repaint();
        v.getPanel3().repaint();
    }

    // -----------------------------
    //      GRAFICAS
    // -----------------------------
    private void actualizarGraficas(Map<String, ModStats> statsPorModulo) {

        String[] modulos = {
            "COMUNICACIÓN ESCRITA",
            "RAZONAMIENTO CUANTITATIVO",
            "LECTURA CRÍTICA",
            "COMPETENCIAS CIUDADANAS",
            "INGLÉS"
        };

        String[] etiquetas = {
            "Com. escrita", "Raz. cuant.", "Lectura", "Comp. ciud.", "Inglés"
        };

        DefaultCategoryDataset dsPromedio = new DefaultCategoryDataset();
        DefaultCategoryDataset dsCv = new DefaultCategoryDataset();

        for (int i = 0; i < modulos.length; i++) {
            ModStats st = statsPorModulo.get(modulos[i]);
            if (st == null || st.n <= 0) {
                continue;
            }

            dsPromedio.addValue(st.media, "Promedio", etiquetas[i]);
            dsCv.addValue(st.cv, "Dispersión %", etiquetas[i]);
        }

        JFreeChart chartPromedio = ChartFactory.createBarChart(
                "Promedio por competencia",
                "Competencia", "Puntaje", dsPromedio,
                PlotOrientation.VERTICAL, false, true, false
        );
        configurarEtiquetas(chartPromedio, "0.0");

        JFreeChart chartCv = ChartFactory.createBarChart(
                "Dispersión por competencia (CV%)",
                "Competencia", "CV (%)", dsCv,
                PlotOrientation.VERTICAL, false, true, false
        );
        configurarEtiquetas(chartCv, "0.0");

        v.getPanel1().removeAll();
        v.getPanel1().add(new ChartPanel(chartPromedio), BorderLayout.CENTER);

        v.getPanel3().removeAll();
        v.getPanel3().add(new ChartPanel(chartCv), BorderLayout.CENTER);

        v.getPanel1().revalidate();
        v.getPanel1().repaint();
        v.getPanel3().revalidate();
        v.getPanel3().repaint();
    }

    private void configurarEtiquetas(JFreeChart chart, String formato) {
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat(formato)));
        renderer.setDefaultItemLabelsVisible(true);
    }

    // -----------------------------
    //      EXPORTACIÓN
    // -----------------------------
    private void exportarReporte() {

        String formato = v.getFormatoSeleccionado();
        if (formato == null) {
            JOptionPane.showMessageDialog(v, "Seleccione PDF o Excel.");
            return;
        }

        if (v.getTabla().getRowCount() == 0) {
            JOptionPane.showMessageDialog(v, "No hay datos para exportar.");
            return;
        }

        if (formato.equals("PDF")) {
            exportarPDF();
        } else {
            exportarExcel();
        }
    }

    private String construirTextoEncabezado() {

        String nombre = "Usuario";
        if (Usuario.usuarioActual != null) {
            String n = Usuario.usuarioActual.getNombre();
            String a = Usuario.usuarioActual.getApellido();
            nombre = (n == null ? "" : n.trim()) + " " + (a == null ? "" : a.trim());
            nombre = nombre.trim().isEmpty() ? "Usuario" : nombre.trim();
        }

        String cargo = "Ejecutivo";
        if (Usuario.usuarioActual != null && Usuario.usuarioActual.getRol() != null) {
            // usamos toString() del rol (es lo que se muestra en el combo)
            String rolStr = Usuario.usuarioActual.getRol().toString();
            if (rolStr != null && !rolStr.trim().isEmpty()) {
                cargo = rolStr.trim();
            }
        }

        return String.format("""
               Universidad de los Llanos
               Programa de Ingeniería de Sistemas
               Reporte de resultados SABER PRO
               
               Cordial saludo %s %s:
               
               A continuación se presentan los resultados obtenidos de acuerdo con la consulta seleccionada.
               
               """, cargo, nombre);
    }

    private void agregarIndicadoresGlobales(Document doc) throws DocumentException {
        com.itextpdf.text.Font titulo
                = new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        12,
                        com.itextpdf.text.Font.BOLD
                );

        Paragraph p = new Paragraph("Indicadores globales del puntaje SABER PRO", titulo);
        p.setSpacingBefore(20);
        p.setSpacingAfter(10);
        doc.add(p);

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(60);

        t.addCell("Total de datos");
        t.addCell(String.valueOf(ultimoN));

        t.addCell("Promedio");
        t.addCell(DF1.format(ultimoMedia));

        t.addCell("Varianza");
        t.addCell(DF2.format(ultimoVar));

        t.addCell("Coeficiente de dispersión (CV%)");
        t.addCell(DF1.format(ultimoCv) + " %");

        doc.add(t);
    }

    private void agregarIndicadoresPorModulo(Document doc) throws DocumentException {

        if (statsUltimaConsulta.isEmpty()) {
            return;
        }

        com.itextpdf.text.Font titulo
                = new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        12,
                        com.itextpdf.text.Font.BOLD
                );

        Paragraph p = new Paragraph("Indicadores por competencia genérica (módulos)", titulo);
        p.setSpacingBefore(20);
        p.setSpacingAfter(10);
        doc.add(p);

        PdfPTable t = new PdfPTable(5);
        t.setWidthPercentage(100);

        t.addCell("Competencia");
        t.addCell("Promedio");
        t.addCell("Varianza");
        t.addCell("Dispersión (CV%)");
        t.addCell("Total estudiantes");

        String[] modulos = {
            "COMUNICACIÓN ESCRITA",
            "RAZONAMIENTO CUANTITATIVO",
            "LECTURA CRÍTICA",
            "COMPETENCIAS CIUDADANAS",
            "INGLÉS"
        };

        for (String m : modulos) {
            ModStats st = statsUltimaConsulta.get(m);
            if (st == null || st.n <= 0) {
                continue;
            }

            t.addCell(m);
            t.addCell(DF1.format(st.media));
            t.addCell(DF2.format(st.varianza));
            t.addCell(DF1.format(st.cv) + " %");
            t.addCell(String.valueOf(st.n));
        }

        doc.add(t);
    }

    private void exportarPDF() {

        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Reporte_SABERPRO.pdf"));

            if (fileChooser.showSaveDialog(v) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fileChooser.getSelectedFile();

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            Paragraph encabezado = new Paragraph(construirTextoEncabezado());
            encabezado.setSpacingAfter(10);
            doc.add(encabezado);

            // tabla con los registros de la consulta
            PdfPTable table = new PdfPTable(v.getTabla().getColumnCount());
            table.setWidthPercentage(100);

            for (int i = 0; i < v.getTabla().getColumnCount(); i++) {
                table.addCell(v.getTabla().getColumnName(i));
            }

            for (int i = 0; i < v.getTabla().getRowCount(); i++) {
                for (int j = 0; j < v.getTabla().getColumnCount(); j++) {
                    table.addCell(String.valueOf(v.getTabla().getValueAt(i, j)));
                }
            }

            doc.add(table);

            // indicadores globales y por módulo
            agregarIndicadoresGlobales(doc);
            agregarIndicadoresPorModulo(doc);

            doc.close();

            JOptionPane.showMessageDialog(v, "PDF generado con éxito.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(v, "Error al generar PDF: " + e.getMessage());
        }
    }

    private void exportarExcel() {

        try {

            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("Reporte_SABERPRO.xlsx"));

            if (fc.showSaveDialog(v) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fc.getSelectedFile();

            Workbook book = new XSSFWorkbook();
            Sheet sheet = book.createSheet("Resultados");

            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);

            for (int i = 0; i < v.getTabla().getColumnCount(); i++) {
                header.createCell(i).setCellValue(v.getTabla().getColumnName(i));
            }

            for (int i = 0; i < v.getTabla().getRowCount(); i++) {
                Row row = sheet.createRow(rowIndex++);

                for (int j = 0; j < v.getTabla().getColumnCount(); j++) {
                    row.createCell(j).setCellValue(String.valueOf(v.getTabla().getValueAt(i, j)));
                }
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                book.write(out);
            }

            JOptionPane.showMessageDialog(v, "Excel generado con éxito.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(v, "Error al generar Excel: " + e.getMessage());
        }
    }
}
