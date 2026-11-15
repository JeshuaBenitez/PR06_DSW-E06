package com.pr06.asistencia.api.service;

import com.pr06.asistencia.api.dto.LecturaIoTDto;
import com.pr06.asistencia.api.dto.LecturaIoTRespuesta;
import com.pr06.asistencia.api.exception.DatosInvalidosException;
import com.pr06.asistencia.api.exception.RecursoNoEncontradoException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

@Service
public class TelemetriaService {
    private final JdbcTemplate jdbc;
    public TelemetriaService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public LecturaIoTRespuesta guardar(LecturaIoTDto d) {
        if (!"AFORO".equals(d.variable) || !"personas".equals(d.unidad)
                || !"SIMULADOR_HTTP".equals(d.origen) || d.valor.stripTrailingZeros().scale() > 0) {
            throw new DatosInvalidosException("Se requiere AFORO entero en personas desde SIMULADOR_HTTP.");
        }
        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
        if (d.fechaHora.isBefore(ahora.minusMinutes(10)) || d.fechaHora.isAfter(ahora.plusMinutes(1))) {
            throw new DatosInvalidosException("La fecha debe estar entre diez minutos antes y un minuto después del servidor.");
        }
        if (jdbc.queryForObject("SELECT COUNT(*) FROM dispositivo WHERE id_dispositivo=? AND estado='ACTIVO'",
                Integer.class, d.idDispositivo) == 0 ||
            jdbc.queryForObject("SELECT COUNT(*) FROM sesion WHERE id_sesion=?", Integer.class, d.idSesion) == 0) {
            throw new RecursoNoEncontradoException("Dispositivo activo o sesión no encontrada.");
        }
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO lectura_iot (id_dispositivo,id_sesion,fecha_hora,variable,unidad,valor,origen) VALUES (?,?,?,?,?,?,?)",
                    new String[]{"id_lectura"});
                ps.setInt(1,d.idDispositivo); ps.setInt(2,d.idSesion);
                ps.setTimestamp(3,Timestamp.from(d.fechaHora.toInstant()));
                ps.setString(4,d.variable); ps.setString(5,d.unidad);
                ps.setBigDecimal(6,d.valor); ps.setString(7,d.origen);
                return ps;
            }, key);
        } catch (DuplicateKeyException e) {
            throw new DatosInvalidosException("Lectura duplicada para el dispositivo y la fecha.");
        }
        return new LecturaIoTRespuesta(key.getKey().longValue(), d.idDispositivo, d.idSesion,
            d.fechaHora, d.variable, d.unidad, d.valor, d.origen);
    }

    public List<LecturaIoTRespuesta> listar(int sesionId, OffsetDateTime desde) {
        if (sesionId < 1 || desde.isBefore(OffsetDateTime.now(ZoneOffset.UTC).minusDays(30))) {
            throw new DatosInvalidosException("Sesión o ventana temporal inválida (máximo treinta días).");
        }
        return jdbc.query("SELECT id_lectura,id_dispositivo,id_sesion,fecha_hora,variable,unidad,valor,origen " +
            "FROM lectura_iot WHERE id_sesion=? AND fecha_hora>=? ORDER BY fecha_hora DESC LIMIT 100",
            (rs,n) -> new LecturaIoTRespuesta(rs.getLong(1),rs.getInt(2),rs.getInt(3),
                rs.getObject(4,OffsetDateTime.class),rs.getString(5),rs.getString(6),rs.getBigDecimal(7),rs.getString(8)),
            sesionId, Timestamp.from(desde.toInstant()));
    }
}
