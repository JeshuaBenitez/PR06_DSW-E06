package com.pr06.asistencia.servlet;

import com.pr06.asistencia.dao.RegistroDAO;
import com.pr06.asistencia.model.Registro;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrosServlet extends HttpServlet {

    private final RegistroDAO registroDAO = new RegistroDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Registro> registros = registroDAO.listarRegistros();
            request.setAttribute("registros", registros);
            request.getRequestDispatcher("/registros.jsp")
                .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                "No se pudieron consultar los registros de asistencia",
                e
            );
        }
    }
}
