package com.saberpro.infrastructure.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import com.saberpro.infrastructure.gateways.InformeGateway;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class InformeGatewayImpl implements InformeGateway {

    private static final String GEMINI_API_KEY =
            System.getenv("GEMINI_API_KEY");
    private static final String GEMINI_MODEL   = "gemini-2.5-pro";
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1/models/";

    @Override
    public String generarInformeIA(String contexto) throws Exception {
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La variable de entorno GEMINI_API_KEY no está configurada.");
        }

        String urlStr = GEMINI_API_URL + GEMINI_MODEL + ":generateContent";
        URL url = new java.net.URI(urlStr).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("x-goog-api-key", GEMINI_API_KEY);
        con.setDoOutput(true);

        // Armar body JSON
        JSONObject part = new JSONObject();
        part.put("text", contexto);

        JSONArray partsArr = new JSONArray();
        partsArr.put(part);

        JSONObject contents = new JSONObject();
        contents.put("parts", partsArr);

        JSONArray contentsWrapper = new JSONArray();
        contentsWrapper.put(contents);

        JSONObject body = new JSONObject();
        body.put("contents", contentsWrapper);

        try (OutputStream os = con.getOutputStream()) {
            os.write(body.toString().getBytes("UTF-8"));
        }

        int status = con.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? con.getInputStream()
                : con.getErrorStream();

        String response;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, "UTF-8"))) {
            response = br.lines().collect(Collectors.joining("\n"));
        }

        if (status < 200 || status >= 300) {
            throw new RuntimeException(
                    "Error en API Gemini (" + status + "): " + response);
        }

        // Parsear respuesta
        JSONObject json       = new JSONObject(response);
        JSONArray  candidates = json.getJSONArray("candidates");

        if (candidates.length() == 0) {
            throw new RuntimeException("Respuesta de Gemini sin 'candidates'.");
        }

        JSONArray parts = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) {
            JSONObject p = parts.getJSONObject(i);
            if (p.has("text")) sb.append(p.getString("text"));
        }

        return sb.toString().trim();
    }

    @Override
    public void guardarInformePDF(String textoInforme, String rutaDestino)
            throws Exception {

        // Asegurar extensión .pdf
        if (!rutaDestino.toLowerCase().endsWith(".pdf")) {
            rutaDestino = rutaDestino + ".pdf";
        }

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try (FileOutputStream fos = new FileOutputStream(rutaDestino)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontTexto  = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

            String[] lineas = textoInforme.split("\\r?\\n");
            boolean tituloAgregado = false;

            for (String linea : lineas) {
                // Limpiar marcado Markdown de negrilla (**)
                String lineaLimpia = linea.replaceAll("\\*\\*(.*?)\\*\\*", "$1").trim();

                if (!tituloAgregado) {
                    Paragraph titulo = new Paragraph(lineaLimpia, fontTitulo);
                    titulo.setAlignment(Element.ALIGN_CENTER);
                    titulo.setSpacingAfter(15f);
                    document.add(titulo);
                    tituloAgregado = true;
                } else {
                    Paragraph p = new Paragraph(lineaLimpia, fontTexto);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    p.setSpacingAfter(4f);
                    document.add(p);
                }
            }

            document.close();
        }
    }
}