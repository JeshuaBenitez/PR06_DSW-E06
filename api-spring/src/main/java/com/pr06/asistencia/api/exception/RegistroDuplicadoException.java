package com.pr06.asistencia.api.exception;

public class RegistroDuplicadoException extends RuntimeException {

    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
