package com.pr06.asistencia.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CambioEstadoSesionDto {

    @NotNull
    @Pattern(regexp = "PROGRAMADA|EN_CURSO|FINALIZADA|CANCELADA")
    private String estado;

    public CambioEstadoSesionDto() {
    }

    public CambioEstadoSesionDto(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
