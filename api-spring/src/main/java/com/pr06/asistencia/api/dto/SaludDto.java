package com.pr06.asistencia.api.dto;

public class SaludDto {

    private final String estado;
    private final String baseDatos;

    public SaludDto(String estado, String baseDatos) {
        this.estado = estado;
        this.baseDatos = baseDatos;
    }

    public String getEstado() {
        return estado;
    }

    public String getBaseDatos() {
        return baseDatos;
    }
}
