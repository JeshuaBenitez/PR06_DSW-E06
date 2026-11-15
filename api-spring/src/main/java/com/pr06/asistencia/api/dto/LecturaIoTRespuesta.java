package com.pr06.asistencia.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class LecturaIoTRespuesta {
    public final Long idLectura;
    public final Integer idDispositivo, idSesion;
    public final OffsetDateTime fechaHora;
    public final String variable, unidad, origen, alerta;
    public final BigDecimal valor;

    public LecturaIoTRespuesta(Long idLectura, Integer idDispositivo, Integer idSesion,
            OffsetDateTime fechaHora, String variable, String unidad, BigDecimal valor, String origen) {
        this.idLectura = idLectura; this.idDispositivo = idDispositivo; this.idSesion = idSesion;
        this.fechaHora = fechaHora; this.variable = variable; this.unidad = unidad;
        this.valor = valor; this.origen = origen;
        this.alerta = valor.compareTo(new BigDecimal("25")) >= 0 ? "AFORO_ALTO" : "NORMAL";
    }
}
