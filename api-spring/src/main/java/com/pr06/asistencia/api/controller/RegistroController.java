package com.pr06.asistencia.api.controller;

import com.pr06.asistencia.api.dto.RegistroDto;
import com.pr06.asistencia.api.dto.RegistroEntradaDto;
import com.pr06.asistencia.api.service.AsistenciaService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistroController {

    private final AsistenciaService asistenciaService;

    public RegistroController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/registros")
    public List<RegistroDto> listarRegistros() {
        return asistenciaService.listarRegistros();
    }

    @PostMapping("/registros")
    public ResponseEntity<RegistroDto> registrarEntrada(
            @Valid @RequestBody RegistroEntradaDto solicitud) {
        RegistroDto registro = asistenciaService.registrarEntrada(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }
}
