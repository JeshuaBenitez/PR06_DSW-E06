<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="com.pr06.asistencia.model.Registro" %>
<%!
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
%>
<%
    @SuppressWarnings("unchecked")
    List<Registro> registros =
        (List<Registro>) request.getAttribute("registros");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registros de asistencia</title>
    <style>
        body { font-family: sans-serif; margin: 2rem; }
        table { border-collapse: collapse; width: 100%; max-width: 1100px; }
        th, td { border: 1px solid #999; padding: 0.5rem; text-align: left; }
        th { background: #eee; }
    </style>
</head>
<body>
    <h1>Registros de asistencia</h1>

    <% if (registros == null || registros.isEmpty()) { %>
        <p>No existen registros de asistencia.</p>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Sesión</th>
                    <th>Participante</th>
                    <th>Tipo</th>
                    <th>Fecha y hora</th>
                    <th>Observación</th>
                </tr>
            </thead>
            <tbody>
                <% for (Registro registro : registros) { %>
                    <tr>
                        <td><%= registro.getIdRegistro() %></td>
                        <td><%= escaparHtml(registro.getNombreSesion()) %></td>
                        <td>
                            <%= escaparHtml(registro.getNombreParticipante()) %>
                        </td>
                        <td><%= escaparHtml(registro.getTipoRegistro()) %></td>
                        <td>
                            <%= registro.getFechaHora() == null
                                ? ""
                                : registro.getFechaHora()
                                    .format(FORMATO_FECHA_HORA) %>
                        </td>
                        <td>
                            <%= registro.getObservacion() == null
                                || registro.getObservacion().isEmpty()
                                ? "Sin observación"
                                : escaparHtml(registro.getObservacion()) %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>

    <p>
        <a href="${pageContext.request.contextPath}/registro">
            Registrar asistencia
        </a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/">Regresar al inicio</a>
    </p>
</body>
</html>
