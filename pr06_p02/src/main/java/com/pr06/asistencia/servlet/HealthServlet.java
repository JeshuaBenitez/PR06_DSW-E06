package com.pr06.asistencia.servlet;

import com.pr06.asistencia.dao.ConexionDB;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        try (Connection conexion = ConexionDB.getConexion()) {

            if (conexion != null && !conexion.isClosed()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(
                    "OK - Conexion a PostgreSQL establecida"
                );
            }

        } catch (SQLException e) {

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println(
                "ERROR - No se pudo conectar a PostgreSQL"
            );

            System.err.println(
                "Error de conexion: " + e.getMessage()
            );
        }
    }
}
