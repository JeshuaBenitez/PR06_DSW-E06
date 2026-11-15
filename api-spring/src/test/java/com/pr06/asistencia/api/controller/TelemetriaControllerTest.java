package com.pr06.asistencia.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pr06.asistencia.api.config.BasicAuthenticationEntryPoint;
import com.pr06.asistencia.api.config.SecurityConfig;
import com.pr06.asistencia.api.dto.LecturaIoTRespuesta;
import com.pr06.asistencia.api.exception.ApiExceptionHandler;
import com.pr06.asistencia.api.service.TelemetriaService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TelemetriaController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, BasicAuthenticationEntryPoint.class})
@TestPropertySource(properties={"pr06.security.username=operador-prueba", "pr06.security.password=clave-prueba-segura"})
class TelemetriaControllerTest {
    @Autowired MockMvc mvc;
    @MockBean TelemetriaService service;

    @Test void consultaPublicaDevuelveHistorialYAlerta() throws Exception {
        when(service.listar(eq(1), any())).thenReturn(Collections.singletonList(
            new LecturaIoTRespuesta(7L,1,1,OffsetDateTime.parse("2026-11-14T12:00:00Z"),
                "AFORO","personas",new BigDecimal("26"),"SIMULADOR_HTTP")));
        mvc.perform(get("/telemetria").param("sesionId","1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].alerta").value("AFORO_ALTO"));
    }

    @Test void escrituraRequiereClaveYValidaCuerpo() throws Exception {
        String json="{\"idDispositivo\":1,\"idSesion\":1,\"fechaHora\":\"2026-11-14T12:00:00Z\",\"variable\":\"AFORO\",\"unidad\":\"personas\",\"valor\":26,\"origen\":\"SIMULADOR_HTTP\"}";
        mvc.perform(post("/telemetria").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/telemetria").with(httpBasic("operador-prueba","clave-prueba-segura"))
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
