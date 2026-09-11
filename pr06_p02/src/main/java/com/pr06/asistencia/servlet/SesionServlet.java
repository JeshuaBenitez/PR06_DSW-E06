package com.pr06.asistencia.servlet;

import com.pr06.asistencia.dao.SesionDAO;
import com.pr06.asistencia.model.Sesion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SesionServlet extends HttpServlet {

    private final SesionDAO sesionDAO = new SesionDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Sesion> sesiones = sesionDAO.obtenerTodas();
            request.setAttribute("sesiones", sesiones);
            request.getRequestDispatcher("/sesiones.jsp")
                .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                "No se pudieron consultar las sesiones",
                e
            );
        }
    }
}
