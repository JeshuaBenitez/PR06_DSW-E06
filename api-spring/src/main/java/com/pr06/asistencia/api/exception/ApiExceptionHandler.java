package com.pr06.asistencia.api.exception;

import com.pr06.asistencia.api.dto.ErrorDto;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorDto> manejarFormatoInvalido(Exception exception) {
        return crearRespuesta(
            HttpStatus.BAD_REQUEST,
            "DATOS_INVALIDOS",
            "Los datos enviados no son válidos."
        );
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorDto> manejarDatosInvalidos(
            DatosInvalidosException exception) {
        return crearRespuesta(
            HttpStatus.BAD_REQUEST,
            "DATOS_INVALIDOS",
            exception.getMessage()
        );
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDto> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException exception) {
        return crearRespuesta(
            HttpStatus.NOT_FOUND,
            "REFERENCIA_NO_ENCONTRADA",
            exception.getMessage()
        );
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<ErrorDto> manejarRegistroDuplicado(
            RegistroDuplicadoException exception) {
        return crearRespuesta(
            HttpStatus.CONFLICT,
            "REGISTRO_DUPLICADO",
            exception.getMessage()
        );
    }

    @ExceptionHandler(TransicionNoPermitidaException.class)
    public ResponseEntity<ErrorDto> manejarTransicionNoPermitida(
            TransicionNoPermitidaException exception) {
        return crearRespuesta(
            HttpStatus.CONFLICT,
            "TRANSICION_NO_PERMITIDA",
            exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> manejarErrorInterno(Exception exception) {
        return crearRespuesta(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ERROR_INTERNO",
            "Ocurrió un error interno."
        );
    }

    private ResponseEntity<ErrorDto> crearRespuesta(
            HttpStatus estado,
            String codigo,
            String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorDto(codigo, mensaje));
    }
}
