package com.pr06.asistencia.api.exception;

public class TransicionNoPermitidaException extends RuntimeException {

    public TransicionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
