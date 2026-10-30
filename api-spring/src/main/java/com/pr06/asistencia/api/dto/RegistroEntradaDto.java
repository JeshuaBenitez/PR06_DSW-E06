package com.pr06.asistencia.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class RegistroEntradaDto {

    @NotNull
    @Positive
    private Integer idSesion;

    @NotNull
    @Positive
    private Integer idParticipante;

    @NotNull
    @Pattern(regexp = "ENTRADA")
    private String tipoRegistro;

    @Size(max = 200)
    private String observacion;

    public RegistroEntradaDto() {
    }

    public RegistroEntradaDto(
            Integer idSesion,
            Integer idParticipante,
            String tipoRegistro,
            String observacion) {
        this.idSesion = idSesion;
        this.idParticipante = idParticipante;
        this.tipoRegistro = tipoRegistro;
        this.observacion = observacion;
    }

    public Integer getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(Integer idSesion) {
        this.idSesion = idSesion;
    }

    public Integer getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(Integer idParticipante) {
        this.idParticipante = idParticipante;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
