<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.pr06.asistencia.model.Sesion" %>
<%!
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
    List<Sesion> sesiones = (List<Sesion>) request.getAttribute("sesiones");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sesiones</title>
    <style>
        body { font-family: sans-serif; margin: 2rem; }
        table { border-collapse: collapse; width: 100%; max-width: 900px; }
        th, td { border: 1px solid #999; padding: 0.5rem; text-align: left; }
        th { background: #eee; }
    </style>
</head>
<body>
    <h1>Sesiones</h1>

    <% if (sesiones == null || sesiones.isEmpty()) { %>
        <p>No existen sesiones registradas.</p>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Fecha</th>
                    <th>Hora de inicio</th>
                    <th>Hora de fin</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                <% for (Sesion sesion : sesiones) { %>
                    <tr>
                        <td><%= sesion.getIdSesion() %></td>
                        <td><%= escaparHtml(sesion.getNombre()) %></td>
                        <td><%= sesion.getFecha() %></td>
                        <td><%= sesion.getHoraInicio() %></td>
                        <td><%= sesion.getHoraFin() %></td>
                        <td><%= escaparHtml(sesion.getEstado()) %></td>
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
