package com.pr06.asistencia.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/pr06_p02";

    private static final String USER =
        "postgres";

    private static final String PASSWORD =
        "1234567890";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "No se encontro el controlador JDBC de PostgreSQL",
                e
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
