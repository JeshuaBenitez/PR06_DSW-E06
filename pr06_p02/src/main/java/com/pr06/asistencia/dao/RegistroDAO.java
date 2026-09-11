package com.pr06.asistencia.dao;

import com.pr06.asistencia.model.Registro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    private static final String INSERTAR_REGISTRO =
        "INSERT INTO registro "
        + "(id_sesion, id_participante, tipo_registro, observacion) "
        + "VALUES (?, ?, ?, ?)";

    private static final String CONSULTAR_EXISTENCIA =
        "SELECT COUNT(*) "
        + "FROM registro "
        + "WHERE id_sesion = ? "
        + "AND id_participante = ? "
        + "AND tipo_registro = ?";

    private static final String LISTAR_REGISTROS =
        "SELECT r.id_registro, r.id_sesion, r.id_participante, "
        + "s.nombre AS nombre_sesion, "
        + "p.nombre AS participante_nombre, "
        + "p.apellido AS participante_apellido, "
        + "r.tipo_registro, r.fecha_hora, r.observacion "
        + "FROM registro r "
        + "INNER JOIN sesion s ON r.id_sesion = s.id_sesion "
        + "INNER JOIN participante p "
        + "ON r.id_participante = p.id_participante "
        + "ORDER BY r.fecha_hora DESC";

    public void insertarRegistro(Registro registro) throws SQLException {
        try (Connection conexion = ConexionDB.getConexion();
                PreparedStatement sentencia =
                    conexion.prepareStatement(INSERTAR_REGISTRO)) {

            sentencia.setInt(1, registro.getIdSesion());
            sentencia.setInt(2, registro.getIdParticipante());
            sentencia.setString(3, registro.getTipoRegistro());
            sentencia.setString(4, registro.getObservacion());

            int filasInsertadas = sentencia.executeUpdate();
            if (filasInsertadas != 1) {
                throw new SQLException("No se pudo insertar el registro");
            }
        }
    }

    public boolean existeRegistro(
            int idSesion,
            int idParticipante,
            String tipoRegistro) throws SQLException {

        try (Connection conexion = ConexionDB.getConexion();
                PreparedStatement sentencia =
                    conexion.prepareStatement(CONSULTAR_EXISTENCIA)) {

            sentencia.setInt(1, idSesion);
            sentencia.setInt(2, idParticipante);
            sentencia.setString(3, tipoRegistro);

            try (ResultSet resultados = sentencia.executeQuery()) {
                return resultados.next() && resultados.getInt(1) > 0;
            }
        }
    }

    public List<Registro> listarRegistros() throws SQLException {
        List<Registro> registros = new ArrayList<>();

        try (Connection conexion = ConexionDB.getConexion();
                PreparedStatement sentencia =
                    conexion.prepareStatement(LISTAR_REGISTROS);
                ResultSet resultados = sentencia.executeQuery()) {

            while (resultados.next()) {
                Timestamp fechaHora = resultados.getTimestamp("fecha_hora");
                String nombreParticipante =
                    resultados.getString("participante_nombre")
                    + " "
                    + resultados.getString("participante_apellido");

                registros.add(new Registro(
                    resultados.getInt("id_registro"),
                    resultados.getInt("id_sesion"),
                    resultados.getInt("id_participante"),
                    resultados.getString("nombre_sesion"),
                    nombreParticipante,
                    resultados.getString("tipo_registro"),
                    fechaHora == null ? null : fechaHora.toLocalDateTime(),
                    resultados.getString("observacion")
                ));
            }
        }

        return registros;
    }
}
