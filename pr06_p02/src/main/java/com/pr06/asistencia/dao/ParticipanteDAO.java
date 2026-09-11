package com.pr06.asistencia.dao;

import com.pr06.asistencia.model.Participante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAO {

    private static final String CONSULTAR_ACTIVOS =
        "SELECT id_participante, codigo_ficticio, nombre, apellido, estado "
        + "FROM participante "
        + "WHERE estado = 'ACTIVO' "
        + "ORDER BY nombre, apellido";

    public List<Participante> obtenerActivos() throws SQLException {
        List<Participante> participantes = new ArrayList<>();

        try (Connection conexion = ConexionDB.getConexion();
                PreparedStatement sentencia =
                    conexion.prepareStatement(CONSULTAR_ACTIVOS);
                ResultSet resultados = sentencia.executeQuery()) {

            while (resultados.next()) {
                participantes.add(new Participante(
                    resultados.getInt("id_participante"),
                    resultados.getString("codigo_ficticio"),
                    resultados.getString("nombre"),
                    resultados.getString("apellido"),
                    resultados.getString("estado")
                ));
            }
        }

        return participantes;
    }
}
