package com.pr06.asistencia.servlet;

import com.pr06.asistencia.dao.ParticipanteDAO;
import com.pr06.asistencia.dao.RegistroDAO;
import com.pr06.asistencia.dao.SesionDAO;
import com.pr06.asistencia.model.Participante;
import com.pr06.asistencia.model.Registro;
import com.pr06.asistencia.model.Sesion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistroServlet extends HttpServlet {

    private static final String TIPO_ENTRADA = "ENTRADA";
    private static final String MENSAJE_DUPLICADO =
        "El participante ya tiene una entrada registrada en esta sesión.";

    private final SesionDAO sesionDAO = new SesionDAO();
    private final ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private final RegistroDAO registroDAO = new RegistroDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            cargarOpcionesFormulario(request);
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                "No se pudo cargar el formulario de registro",
                e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        List<Sesion> sesiones;
        List<Participante> participantes;

        try {
            sesiones = sesionDAO.obtenerTodas();
            participantes = participanteDAO.obtenerActivos();
        } catch (SQLException e) {
            throw new ServletException(
                "No se pudieron validar los datos del formulario",
                e
            );
        }

        request.setAttribute("sesiones", sesiones);
        request.setAttribute("participantes", participantes);

        Integer idSesion = convertirId(request.getParameter("idSesion"));
        Integer idParticipante =
            convertirId(request.getParameter("idParticipante"));
        String observacion = limpiarObservacion(
            request.getParameter("observacion")
        );

        if (idSesion == null || !existeSesion(sesiones, idSesion)) {
            request.setAttribute(
                "mensajeError",
                "Debe seleccionar una sesión válida."
            );
            mostrarFormulario(request, response);
            return;
        }

        if (idParticipante == null
                || !existeParticipante(participantes, idParticipante)) {
            request.setAttribute(
                "mensajeError",
                "Debe seleccionar un participante activo válido."
            );
            mostrarFormulario(request, response);
            return;
        }

        if (observacion != null && observacion.length() > 200) {
            request.setAttribute(
                "mensajeError",
                "La observación no puede exceder 200 caracteres."
            );
            mostrarFormulario(request, response);
            return;
        }

        try {
            if (registroDAO.existeRegistro(
                    idSesion,
                    idParticipante,
                    TIPO_ENTRADA)) {
                request.setAttribute("mensajeDuplicado", MENSAJE_DUPLICADO);
            } else {
                Registro registro = new Registro(
                    idSesion,
                    idParticipante,
                    TIPO_ENTRADA,
                    observacion
                );

                registroDAO.insertarRegistro(registro);
                request.setAttribute(
                    "mensajeExito",
                    "Entrada registrada correctamente."
                );
            }
        } catch (SQLException e) {
            if (esViolacionUnica(e)) {
                request.setAttribute("mensajeDuplicado", MENSAJE_DUPLICADO);
            } else {
                getServletContext().log(
                    "Error al registrar la entrada",
                    e
                );
                request.setAttribute(
                    "mensajeError",
                    "No se pudo registrar la entrada. Intente nuevamente."
                );
            }
        }

        mostrarFormulario(request, response);
    }

    private void cargarOpcionesFormulario(HttpServletRequest request)
            throws SQLException {
        request.setAttribute("sesiones", sesionDAO.obtenerTodas());
        request.setAttribute(
            "participantes",
            participanteDAO.obtenerActivos()
        );
    }

    private void mostrarFormulario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/registro.jsp")
            .forward(request, response);
    }

    private Integer convertirId(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valor);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String limpiarObservacion(String observacion) {
        if (observacion == null || observacion.trim().isEmpty()) {
            return null;
        }

        return observacion.trim();
    }

    private boolean existeSesion(List<Sesion> sesiones, int idSesion) {
        for (Sesion sesion : sesiones) {
            if (sesion.getIdSesion() == idSesion) {
                return true;
            }
        }
        return false;
    }

    private boolean existeParticipante(
            List<Participante> participantes,
            int idParticipante) {
        for (Participante participante : participantes) {
            if (participante.getIdParticipante() == idParticipante) {
                return true;
            }
        }
        return false;
    }

    private boolean esViolacionUnica(SQLException excepcion) {
        SQLException actual = excepcion;

        while (actual != null) {
            if ("23505".equals(actual.getSQLState())) {
                return true;
            }
            actual = actual.getNextException();
        }

        return false;
    }
}
