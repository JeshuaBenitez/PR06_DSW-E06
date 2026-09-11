package com.pr06.asistencia.model;

import java.time.LocalDateTime;

public class Registro {

    private final int idRegistro;
    private final int idSesion;
    private final int idParticipante;
    private final String nombreSesion;
    private final String nombreParticipante;
    private final String tipoRegistro;
    private final LocalDateTime fechaHora;
    private final String observacion;

    public Registro(
            int idRegistro,
            int idSesion,
            int idParticipante,
            String tipoRegistro,
            LocalDateTime fechaHora,
            String observacion) {
        this(
            idRegistro,
            idSesion,
            idParticipante,
            null,
            null,
            tipoRegistro,
            fechaHora,
            observacion
        );
    }

    public Registro(
            int idRegistro,
            int idSesion,
            int idParticipante,
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

    public Registro(
            int idSesion,
            int idParticipante,
            String tipoRegistro,
            String observacion) {
        this(0, idSesion, idParticipante, tipoRegistro, null, observacion);
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public int getIdParticipante() {
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
