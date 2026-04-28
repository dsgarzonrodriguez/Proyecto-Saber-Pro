package com.mycompany.saberpro.Controlador;

import com.mycompany.saberpro.Modelo.Conexion;
import com.mycompany.saberpro.Modelo.Usuario;
import com.mycompany.saberpro.Vista.InterGenerarInforme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONObject;

public class CtrlGenerarInforme {

    private final InterGenerarInforme v;
    private final DecimalFormat DF1 = new DecimalFormat("#,##0.0");
    private final DecimalFormat DF2 = new DecimalFormat("#,##0.00");

    private final JLabel lblCargando;
    private final JProgressBar barraCarga;

    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    // modelo base (sin "models/" y sin ":generateContent")
    private static final String GEMINI_MODEL = "gemini-2.5-pro";
    // endpoint base de Gemini (versión estable v1)
    private static final String GEMINI_API_URL
            = "https://generativelanguage.googleapis.com/v1/models/";

    public CtrlGenerarInforme(InterGenerarInforme vista) {
        this.v = vista;

        JButton btnGenerar = v.getBtnGenerar();
        JButton btnLimpiar = v.getBtnLimpiar();
        JButton btnGuardar = v.getBtnGuardar();  // botón "Guardar PDF"

        this.lblCargando = v.getLabel();
        this.barraCarga = v.getCarga();

        if (btnGenerar != null) {
            btnGenerar.addActionListener(e -> generarInforme());
        }
        if (btnLimpiar != null) {
            btnLimpiar.addActionListener(e -> v.limpiarFiltrosYTexto());
        }
        if (btnGuardar != null) {
            btnGuardar.setText("Guardar PDF"); // por si acaso
            btnGuardar.addActionListener(e -> v.guardarInformeComoPDF());
        }
    }

    // Pequeño contenedor para indicadores por módulo
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

    private void generarInforme() {

        final JButton b = v.getBtnGenerar();
        if (b != null) {
            b.setEnabled(false);
        }

        // Mostramos indicador de carga
        if (lblCargando != null) {
            lblCargando.setVisible(true);
        }
        if (barraCarga != null) {
            barraCarga.setVisible(true);
            barraCarga.setIndeterminate(true);
        }

        final Integer anio = v.getAnio();
        final Integer semestre = v.getSemestre();
        final String programa = v.getProgramaTexto();
        final String ciudad = v.getCiudadTexto();

        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                // =======================
                // 1. ARMAR WHERE DINÁMICO
                // =======================
                StringBuilder where = new StringBuilder(" WHERE 1=1 ");
                List<Object> params = new ArrayList<>();

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

                long n = 0;
                double media = 0, var = 0, sd = 0, cv = 0;

                Map<String, ModStats> statsPorModulo = new HashMap<>();

                // =======================
                // 2. CONSULTAR BD
                // =======================
                try (Connection con = new Conexion().getConexion()) {

                    // --- Indicadores globales ---
                    String sqlKpi
                            = "SELECT COUNT(x) AS n, "
                            + "COALESCE(AVG(x),0) AS media, "
                            + "COALESCE(VAR_SAMP(x),0) AS varianza, "
                            + "COALESCE(STDDEV_SAMP(x),0) AS sd "
                            + "FROM (SELECT puntaje_global AS x "
                            + "      FROM vista_resultados_detalle "
                            + where
                            + "      AND puntaje_global IS NOT NULL "
                            + "      AND puntaje_global > 0) t";

                    try (PreparedStatement ps = con.prepareStatement(sqlKpi)) {
                        for (int i = 0; i < params.size(); i++) {
                            ps.setObject(i + 1, params.get(i));
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                n = rs.getLong("n");
                                media = rs.getDouble("media");
                                var = rs.getDouble("varianza");
                                sd = rs.getDouble("sd");
                            }
                        }
                    }

                    if (n == 0) {
                        return "No se encontraron datos para los filtros seleccionados. "
                                + "No es posible generar un informe significativo.";
                    }

                    cv = (media != 0) ? (sd / media) * 100.0 : 0.0;

                    // --- Indicadores por módulo ---
                    String sqlKpiMod
                            = "SELECT modulo, "
                            + "COUNT(*) AS n, "
                            + "COALESCE(AVG(puntaje_modulo),0) AS media, "
                            + "COALESCE(VAR_SAMP(puntaje_modulo),0) AS varianza, "
                            + "COALESCE(STDDEV_SAMP(puntaje_modulo),0) AS sd "
                            + "FROM vista_resultados_modulo_detalle "
                            + where
                            + " AND puntaje_modulo IS NOT NULL "
                            + " AND puntaje_modulo > 0 "
                            + "GROUP BY modulo";

                    try (PreparedStatement ps = con.prepareStatement(sqlKpiMod)) {
                        for (int i = 0; i < params.size(); i++) {
                            ps.setObject(i + 1, params.get(i));
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String modulo = rs.getString("modulo");
                                long nM = rs.getLong("n");
                                double mediaM = rs.getDouble("media");
                                double varM = rs.getDouble("varianza");
                                double sdM = rs.getDouble("sd");
                                double cvM = (mediaM != 0) ? (sdM / mediaM) * 100.0 : 0.0;

                                if (modulo != null) {
                                    statsPorModulo.put(
                                            modulo.trim().toUpperCase(),
                                            new ModStats(nM, mediaM, varM, cvM)
                                    );
                                }
                            }
                        }
                    }
                }

                // =======================
                // 3. ARMAR CONTEXTO TEXTO
                // =======================
                String descAnio = (anio != null) ? ("Año " + anio) : "todos los años";
                String descSemestre = (semestre != null) ? ("semestre " + semestre) : "todos los semestres";
                String descPrograma = (programa != null) ? programa : "todos los programas";
                String descCiudad = (ciudad != null) ? ciudad : "todas las ciudades";

                String nombreUsuario = (Usuario.usuarioActual != null)
                        ? Usuario.usuarioActual.getNombre() + " " + Usuario.usuarioActual.getApellido()
                        : "Usuario";

                String rolUsuario = (Usuario.usuarioActual != null
                        && Usuario.usuarioActual.getRol() != null
                        && Usuario.usuarioActual.getRol().getNombre() != null)
                        ? Usuario.usuarioActual.getRol().getNombre()
                        : "Directivo";

                StringBuilder contexto = new StringBuilder();

                contexto.append("Datos de resultados SABER PRO de la Universidad de los Llanos.\n");
                contexto.append("Programa(s): ").append(descPrograma).append("\n");
                contexto.append("Cobertura temporal: ").append(descAnio).append(", ").append(descSemestre).append("\n");
                contexto.append("Ciudad(es): ").append(descCiudad).append("\n\n");

                contexto.append("Resumen global del puntaje global:\n");
                contexto.append(" - Número de estudiantes: ").append(n).append("\n");
                contexto.append(" - Puntaje promedio: ").append(DF1.format(media)).append("\n");
                contexto.append(" - Varianza: ").append(DF2.format(var)).append("\n");
                contexto.append(" - Coeficiente de variación (CV%): ").append(DF1.format(cv)).append("\n\n");

                contexto.append("Resultados por competencia (módulos):\n");

                String[] ordenModulos = {
                    "COMUNICACIÓN ESCRITA",
                    "RAZONAMIENTO CUANTITATIVO",
                    "LECTURA CRÍTICA",
                    "COMPETENCIAS CIUDADANAS",
                    "INGLÉS"
                };

                for (String m : ordenModulos) {
                    ModStats st = statsPorModulo.get(m);
                    if (st != null && st.n > 0) {
                        contexto.append(" - ").append(m).append(": ")
                                .append("n=").append(st.n)
                                .append(", promedio=").append(DF1.format(st.media))
                                .append(", varianza=").append(DF2.format(st.varianza))
                                .append(", CV%=").append(DF1.format(st.cv))
                                .append("\n");
                    }
                }

                contexto.append("\nEl informe debe estar dirigido a: ")
                        .append(rolUsuario).append(" ").append(nombreUsuario).append(".\n\n");

                // ====== INSTRUCCIONES PARA QUE EL INFORME QUEDE "BONITO" ======
                contexto.append(
                        "Con esta información, elabora un INFORME ANALÍTICO en español, "
                        + "dirigido a un coordinador SABER PRO.\n"
                        + "Requisitos de estilo:\n"
                        + "- NO incluyas saludo ni despedida, ni secciones de 'Para:' o 'De:'.\n"
                        + "- Usa un título principal en MAYÚSCULAS y en negrilla (formato Markdown: **TITULO**).\n"
                        + "- Usa secciones numeradas con subtítulos en negrilla, por ejemplo:\n"
                        + "  1. CONTEXTO DE LA COHORTE\n"
                        + "  2. ANÁLISIS GLOBAL DE RESULTADOS\n"
                        + "  3. ANÁLISIS POR COMPETENCIAS (FORTALEZAS Y DEBILIDADES)\n"
                        + "  4. TENDENCIAS E HIPÓTESIS EXPLICATIVAS\n"
                        + "  5. CONCLUSIONES CLAVE\n"
                        + "  6. PLAN DE ACCIÓN Y RECOMENDACIONES\n"
                        + "- Usa párrafos claros y listas con viñetas para las recomendaciones.\n"
                        + "- En el PLAN DE ACCIÓN, presenta una lista de acciones donde para cada acción indiques: "
                        + "prioridad, posible responsable y plazo estimado.\n"
                        + "Devuelve todo en texto plano usando negrilla con el formato Markdown (**así**), "
                        + "para que pueda copiarse y pegarse en Word.\n"
                );

                // =======================
                // 4. LLAMAR A GEMINI
                // =======================
                String respuestaIA = llamarGemini(contexto.toString());
                return respuestaIA;
            }

            @Override
            protected void done() {
                try {
                    String informe = get();
                    v.getTxtInforme().setText(informe);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(v,
                            "Error al generar informe con IA (Gemini): " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // OCULTAMOS INDICADOR DE CARGA
                    if (barraCarga != null) {
                        barraCarga.setIndeterminate(false);
                        barraCarga.setVisible(false);
                    }
                    if (lblCargando != null) {
                        lblCargando.setVisible(false);
                    }

                    if (b != null) {
                        b.setEnabled(true);
                    }
                }
            }

        }.execute();
    }

    // ===========================
    //     LLAMADA A GEMINI
    // ===========================
    private String llamarGemini(String prompt) throws Exception {

        if (GEMINI_API_KEY == null || GEMINI_API_KEY.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La variable de entorno GEMINI_API_KEY no está configurada.");
        }

        // Construimos la URL usando el modelo
        String urlStr = GEMINI_API_URL + GEMINI_MODEL + ":generateContent";
        URL url = new URL(urlStr);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("x-goog-api-key", GEMINI_API_KEY);
        con.setDoOutput(true);

        // ===== cuerpo JSON según la API de Gemini =====
        JSONObject contents = new JSONObject();
        JSONArray partsArr = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        partsArr.put(part);
        contents.put("parts", partsArr);

        JSONObject body = new JSONObject();
        JSONArray contentsWrapper = new JSONArray();
        contentsWrapper.put(contents);
        body.put("contents", contentsWrapper);

        String jsonBody = body.toString();

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
        }

        int status = con.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? con.getInputStream()
                : con.getErrorStream();

        String response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            response = br.lines().collect(Collectors.joining("\n"));
        }

        if (status < 200 || status >= 300) {
            throw new RuntimeException("Error en API Gemini (" + status + "): " + response);
        }

        JSONObject json = new JSONObject(response);
        JSONArray candidates = json.getJSONArray("candidates");
        if (candidates.length() == 0) {
            throw new RuntimeException("Respuesta de Gemini sin 'candidates'.");
        }

        JSONObject first = candidates.getJSONObject(0);
        JSONArray parts = first
                .getJSONObject("content")
                .getJSONArray("parts");

        // normalmente viene en el primer part.text
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) {
            JSONObject p = parts.getJSONObject(i);
            if (p.has("text")) {
                sb.append(p.getString("text"));
            }
        }
        return sb.toString().trim();
    }

    // Utilidad por si luego la necesitas
    @SuppressWarnings("unused")
    private Double getDouble(ResultSet rs, String col) throws Exception {
        BigDecimal bd = rs.getBigDecimal(col);
        return (bd != null) ? bd.doubleValue() : null;
    }
}
