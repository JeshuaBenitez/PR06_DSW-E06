package com.pr06.asistencia.dao;

import com.pr06.asistencia.model.Sesion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SesionDAO {

    private static final String CONSULTAR_TODAS =
        "SELECT id_sesion, nombre, fecha, hora_inicio, hora_fin, "
        + "estado, descripcion "
        + "FROM sesion "
        + "ORDER BY fecha, hora_inicio";

    public List<Sesion> obtenerTodas() throws SQLException {
        List<Sesion> sesiones = new ArrayList<>();

        try (Connection conexion = ConexionDB.getConexion();
                PreparedStatement sentencia =
                    conexion.prepareStatement(CONSULTAR_TODAS);
                ResultSet resultados = sentencia.executeQuery()) {

            while (resultados.next()) {
                sesiones.add(new Sesion(
                    resultados.getInt("id_sesion"),
                    resultados.getString("nombre"),
                    resultados.getDate("fecha").toLocalDate(),
                    resultados.getTime("hora_inicio").toLocalTime(),
                    resultados.getTime("hora_fin").toLocalTime(),
                    resultados.getString("estado"),
                    resultados.getString("descripcion")
                ));
            }
        }

        return sesiones;
    }
}
