package com.pr06.asistencia.api.controller;

import com.pr06.asistencia.api.dto.ErrorDto;
import com.pr06.asistencia.api.dto.SaludDto;
import com.pr06.asistencia.api.service.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<?> verificarSalud() {
        if (healthService.baseDatosDisponible()) {
            return ResponseEntity.ok(new SaludDto("OK", "DISPONIBLE"));
        }

        ErrorDto error = new ErrorDto(
            "SERVICIO_NO_DISPONIBLE",
            "La base de datos no está disponible."
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
