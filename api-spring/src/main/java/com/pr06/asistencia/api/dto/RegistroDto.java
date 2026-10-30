package com.pr06.asistencia.api.dto;

import java.time.LocalDateTime;

public class RegistroDto {

    private final Integer idRegistro;
    private final Integer idSesion;
    private final Integer idParticipante;
    private final String nombreSesion;
    private final String nombreParticipante;
    private final String tipoRegistro;
    private final LocalDateTime fechaHora;
    private final String observacion;

    public RegistroDto(
            Integer idRegistro,
            Integer idSesion,
            Integer idParticipante,
            String nombreSesion,
            String nombreParticipante,
            String tipoRegistro,
            LocalDateTime fechaHora,
            String observacion) {
        this.idRegistro = idRegistro;
        this.idSesion = idSesion;
        this.idParticipante = idParticipante;
        this.nombreSesion = nombreSesion;
        this.nombreParticipante = nombreParticipante;
        this.tipoRegistro = tipoRegistro;
        this.fechaHora = fechaHora;
        this.observacion = observacion;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public Integer getIdSesion() {
        return idSesion;
    }

    public Integer getIdParticipante() {
        return idParticipante;
    }

    public String getNombreSesion() {
        return nombreSesion;
    }

    public String getNombreParticipante() {
        return nombreParticipante;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getObservacion() {
        return observacion;
    }
}
