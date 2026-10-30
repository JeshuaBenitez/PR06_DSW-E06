package com.pr06.asistencia.api.controller;

import com.pr06.asistencia.api.dto.CambioEstadoSesionDto;
import com.pr06.asistencia.api.dto.SesionDto;
import com.pr06.asistencia.api.service.AsistenciaService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SesionController {

    private final AsistenciaService asistenciaService;

    public SesionController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/sesiones")
    public List<SesionDto> listarSesiones() {
        return asistenciaService.listarSesiones();
    }

    @PatchMapping("/sesiones/{idSesion}/estado")
    public SesionDto cambiarEstado(
            @PathVariable Integer idSesion,
            @Valid @RequestBody CambioEstadoSesionDto solicitud) {
        return asistenciaService.cambiarEstadoSesion(idSesion, solicitud);
    }
}
