package com.pr06.asistencia.api.controller;

import com.pr06.asistencia.api.dto.LecturaIoTDto;
import com.pr06.asistencia.api.dto.LecturaIoTRespuesta;
import com.pr06.asistencia.api.service.TelemetriaService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telemetria")
public class TelemetriaController {
    private final TelemetriaService service;
    public TelemetriaController(TelemetriaService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LecturaIoTRespuesta crear(@Valid @RequestBody LecturaIoTDto dato) { return service.guardar(dato); }

    @GetMapping
    public List<LecturaIoTRespuesta> listar(@RequestParam(defaultValue="1") int sesionId,
            @RequestParam(required=false) OffsetDateTime desde) {
        return service.listar(sesionId, desde == null ? OffsetDateTime.now(ZoneOffset.UTC).minusHours(24) : desde);
    }
}
