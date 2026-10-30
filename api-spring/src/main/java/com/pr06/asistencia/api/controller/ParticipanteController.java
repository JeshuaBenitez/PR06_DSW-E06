package com.pr06.asistencia.api.controller;

import com.pr06.asistencia.api.dto.ParticipanteDto;
import com.pr06.asistencia.api.service.AsistenciaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipanteController {

    private final AsistenciaService asistenciaService;

    public ParticipanteController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/participantes")
    public List<ParticipanteDto> listarParticipantesActivos() {
        return asistenciaService.listarParticipantesActivos();
    }
}
