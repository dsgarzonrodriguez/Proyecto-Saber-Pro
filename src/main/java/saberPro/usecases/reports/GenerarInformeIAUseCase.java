package saberPro.usecases.reports;

import saberPro.entities.FiltrosConsulta;
import saberPro.infrastructure.gateways.InformeGateway;
import saberPro.usecases.ports.InformePort;
import saberPro.usecases.ports.InformePort.EstadisticasGlobales;
import saberPro.usecases.ports.InformePort.EstadisticasModulo;

import java.text.DecimalFormat;
import java.util.Map;

public class GenerarInformeIAUseCase {

    private static final DecimalFormat DF1 = new DecimalFormat("#,##0.0");
    private static final DecimalFormat DF2 = new DecimalFormat("#,##0.00");

    private static final String[] ORDEN_MODULOS = {
        "COMUNICACIÓN ESCRITA",
        "RAZONAMIENTO CUANTITATIVO",
        "LECTURA CRÍTICA",
        "COMPETENCIAS CIUDADANAS",
        "INGLÉS"
    };

    private final InformePort informePort;
    private final InformeGateway informeGateway;

    public GenerarInformeIAUseCase(InformePort informePort, InformeGateway informeGateway) {
        this.informePort    = informePort;
        this.informeGateway = informeGateway;
    }

    public String ejecutar(FiltrosConsulta filtros, String nombreUsuario, String rolUsuario)
            throws Exception {

        if (filtros == null) {
            throw new IllegalArgumentException("Los filtros son obligatorios.");
        }

        EstadisticasGlobales globales = informePort.consultarEstadisticasGlobales(filtros);

        if (globales.n == 0) {
            throw new IllegalArgumentException(
                    "No se encontraron datos para los filtros seleccionados. " +
                    "No es posible generar un informe significativo.");
        }

        Map<String, EstadisticasModulo> porModulo =
                informePort.consultarEstadisticasPorModulo(filtros);

        String contexto = armarContexto(filtros, globales, porModulo, nombreUsuario, rolUsuario);

        return informeGateway.generarInformeIA(contexto);
    }

    private String armarContexto(FiltrosConsulta filtros,
                                  EstadisticasGlobales globales,
                                  Map<String, EstadisticasModulo> porModulo,
                                  String nombreUsuario,
                                  String rolUsuario) {

        String descAnio      = filtros.getAnio()      != null ? "Año " + filtros.getAnio()           : "todos los años";
        String descSemestre  = filtros.getSemestre()  != null ? "semestre " + filtros.getSemestre()  : "todos los semestres";
        String descPrograma  = filtros.getIdPrograma() != null ? "programa ID " + filtros.getIdPrograma() : "todos los programas";
        String descCiudad    = filtros.getIdModulo()  != null ? "módulo ID " + filtros.getIdModulo() : "todas las ciudades";

        StringBuilder ctx = new StringBuilder();

        ctx.append("Datos de resultados SABER PRO de la Universidad de los Llanos.\n");
        ctx.append("Programa(s): ").append(descPrograma).append("\n");
        ctx.append("Cobertura temporal: ").append(descAnio).append(", ").append(descSemestre).append("\n");
        ctx.append("Ciudad(es): ").append(descCiudad).append("\n\n");

        ctx.append("Resumen global del puntaje global:\n");
        ctx.append(" - Número de estudiantes: ").append(globales.n).append("\n");
        ctx.append(" - Puntaje promedio: ").append(DF1.format(globales.media)).append("\n");
        ctx.append(" - Varianza: ").append(DF2.format(globales.varianza)).append("\n");
        ctx.append(" - Coeficiente de variación (CV%): ").append(DF1.format(globales.cv)).append("\n\n");

        ctx.append("Resultados por competencia (módulos):\n");
        for (String modulo : ORDEN_MODULOS) {
            EstadisticasModulo st = porModulo.get(modulo);
            if (st != null && st.n > 0) {
                ctx.append(" - ").append(modulo).append(": ")
                   .append("n=").append(st.n)
                   .append(", promedio=").append(DF1.format(st.media))
                   .append(", varianza=").append(DF2.format(st.varianza))
                   .append(", CV%=").append(DF1.format(st.cv))
                   .append("\n");
            }
        }

        ctx.append("\nEl informe debe estar dirigido a: ")
           .append(rolUsuario).append(" ").append(nombreUsuario).append(".\n\n");

        ctx.append(
            "Con esta información, elabora un INFORME ANALÍTICO en español, " +
            "dirigido a un coordinador SABER PRO.\n" +
            "Requisitos de estilo:\n" +
            "- NO incluyas saludo ni despedida, ni secciones de 'Para:' o 'De:'.\n" +
            "- Usa un título principal en MAYÚSCULAS y en negrilla (formato Markdown: **TITULO**).\n" +
            "- Usa secciones numeradas con subtítulos en negrilla, por ejemplo:\n" +
            "  1. CONTEXTO DE LA COHORTE\n" +
            "  2. ANÁLISIS GLOBAL DE RESULTADOS\n" +
            "  3. ANÁLISIS POR COMPETENCIAS (FORTALEZAS Y DEBILIDADES)\n" +
            "  4. TENDENCIAS E HIPÓTESIS EXPLICATIVAS\n" +
            "  5. CONCLUSIONES CLAVE\n" +
            "  6. PLAN DE ACCIÓN Y RECOMENDACIONES\n" +
            "- Usa párrafos claros y listas con viñetas para las recomendaciones.\n" +
            "- En el PLAN DE ACCIÓN, presenta una lista de acciones donde para cada acción indiques: " +
            "prioridad, posible responsable y plazo estimado.\n" +
            "Devuelve todo en texto plano usando negrilla con el formato Markdown (**así**), " +
            "para que pueda copiarse y pegarse en Word.\n"
        );

        return ctx.toString();
    }
}