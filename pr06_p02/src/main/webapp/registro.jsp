<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.pr06.asistencia.model.Sesion" %>
<%@ page import="com.pr06.asistencia.model.Participante" %>
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

    @SuppressWarnings("unchecked")
    List<Participante> participantes =
        (List<Participante>) request.getAttribute("participantes");

    String mensajeExito = (String) request.getAttribute("mensajeExito");
    String mensajeError = (String) request.getAttribute("mensajeError");
    String mensajeDuplicado =
        (String) request.getAttribute("mensajeDuplicado");
    String idSesionSeleccionada = request.getParameter("idSesion");
    String idParticipanteSeleccionado =
        request.getParameter("idParticipante");
    String observacion = request.getParameter("observacion");
    boolean formularioDisponible =
        sesiones != null && !sesiones.isEmpty()
        && participantes != null && !participantes.isEmpty();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar asistencia</title>
    <style>
        body { font-family: sans-serif; margin: 2rem; }
        form { max-width: 600px; }
        label { display: block; margin-top: 1rem; }
        select, input, textarea, button {
            box-sizing: border-box;
            display: block;
            margin-top: 0.3rem;
            max-width: 100%;
            padding: 0.5rem;
            width: 100%;
        }
        textarea { min-height: 6rem; }
        button { cursor: pointer; margin-top: 1.2rem; }
        .exito { color: #176b2c; }
        .error, .duplicado { color: #a11; }
    </style>
</head>
<body>
    <h1>Registrar asistencia</h1>

    <% if (mensajeExito != null) { %>
        <p class="exito"><%= escaparHtml(mensajeExito) %></p>
    <% } %>
    <% if (mensajeDuplicado != null) { %>
        <p class="duplicado"><%= escaparHtml(mensajeDuplicado) %></p>
    <% } %>
    <% if (mensajeError != null) { %>
        <p class="error"><%= escaparHtml(mensajeError) %></p>
    <% } %>

    <% if (!formularioDisponible) { %>
        <p class="error">
            Se requieren sesiones y participantes activos para registrar una entrada.
        </p>
    <% } %>

    <form method="post"
          action="${pageContext.request.contextPath}/registro">
        <label for="idSesion">Sesión</label>
        <select id="idSesion" name="idSesion" required>
            <option value="">Seleccione una sesión</option>
            <% if (sesiones != null) {
                for (Sesion sesion : sesiones) {
                    String id = String.valueOf(sesion.getIdSesion());
            %>
                <option value="<%= id %>"
                    <%= id.equals(idSesionSeleccionada) ? "selected" : "" %>>
                    <%= sesion.getFecha() %> -
                    <%= escaparHtml(sesion.getNombre()) %>
                </option>
            <%  }
               } %>
        </select>

        <label for="idParticipante">Participante</label>
        <select id="idParticipante" name="idParticipante" required>
            <option value="">Seleccione un participante</option>
            <% if (participantes != null) {
                for (Participante participante : participantes) {
                    String id = String.valueOf(
                        participante.getIdParticipante()
                    );
            %>
                <option value="<%= id %>"
                    <%= id.equals(idParticipanteSeleccionado)
                        ? "selected" : "" %>>
                    <%= escaparHtml(participante.getCodigoFicticio()) %> -
                    <%= escaparHtml(participante.getNombre()) %>
                    <%= escaparHtml(participante.getApellido()) %>
                </option>
            <%  }
               } %>
        </select>

        <label for="tipoRegistro">Tipo de registro</label>
        <input id="tipoRegistro" type="text" value="ENTRADA" readonly>

        <label for="observacion">Observación (opcional)</label>
        <textarea id="observacion" name="observacion"
                  maxlength="200"><%= escaparHtml(observacion) %></textarea>

        <button type="submit" <%= formularioDisponible ? "" : "disabled" %>>
            Registrar entrada
        </button>
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/registros">
            Ver registros de asistencia
        </a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/">Regresar al inicio</a>
    </p>
</body>
</html>
