package com.pr06.asistencia.api.dto;

public class ErrorDto {

    private final String codigo;
    private final String mensaje;

    public ErrorDto(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
