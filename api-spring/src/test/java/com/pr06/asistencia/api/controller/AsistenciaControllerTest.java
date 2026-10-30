package com.pr06.asistencia.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pr06.asistencia.api.config.BasicAuthenticationEntryPoint;
import com.pr06.asistencia.api.config.SecurityConfig;
import com.pr06.asistencia.api.dto.ParticipanteDto;
import com.pr06.asistencia.api.dto.RegistroDto;
import com.pr06.asistencia.api.dto.SesionDto;
import com.pr06.asistencia.api.exception.ApiExceptionHandler;
import com.pr06.asistencia.api.exception.RecursoNoEncontradoException;
import com.pr06.asistencia.api.exception.RegistroDuplicadoException;
import com.pr06.asistencia.api.exception.TransicionNoPermitidaException;
import com.pr06.asistencia.api.service.AsistenciaService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
    SesionController.class,
    ParticipanteController.class,
    RegistroController.class
})
@Import({
    ApiExceptionHandler.class,
    SecurityConfig.class,
    BasicAuthenticationEntryPoint.class
})
@TestPropertySource(properties = {
    "pr06.security.username=operador-prueba",
    "pr06.security.password=clave-prueba-segura"
})
class AsistenciaControllerTest {

    private static final String USUARIO = "operador-prueba";
    private static final String CLAVE = "clave-prueba-segura";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsistenciaService asistenciaService;

    @Test
    void consultasResponden200ConEstructuraDelContrato() throws Exception {
        when(asistenciaService.listarSesiones()).thenReturn(Collections.singletonList(
            new SesionDto(
                1,
                "Sesión",
                LocalDate.of(2026, 9, 15),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "PROGRAMADA",
                null
            )
        ));
        when(asistenciaService.listarParticipantesActivos())
            .thenReturn(Collections.singletonList(
                new ParticipanteDto(2, "P002", "Ana", "López", "ACTIVO")
            ));
        when(asistenciaService.listarRegistros()).thenReturn(Collections.singletonList(
            registroRespuesta()
        ));

        mockMvc.perform(get("/sesiones"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idSesion").value(1))
            .andExpect(jsonPath("$[0].horaInicio").value("09:00:00"));
        mockMvc.perform(get("/participantes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
        mockMvc.perform(get("/registros"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tipoRegistro").value("ENTRADA"));
    }

    @Test
    void consultasSinDatosRespondenArreglosVacios() throws Exception {
        when(asistenciaService.listarSesiones()).thenReturn(Collections.emptyList());
        when(asistenciaService.listarParticipantesActivos())
            .thenReturn(Collections.emptyList());
        when(asistenciaService.listarRegistros()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/sesiones"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
        mockMvc.perform(get("/participantes"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
        mockMvc.perform(get("/registros"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void registroValidoResponde201() throws Exception {
        when(asistenciaService.registrarEntrada(any())).thenReturn(registroRespuesta());

        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "ENTRADA", "Evidencia")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idRegistro").value(9))
            .andExpect(jsonPath("$.fechaHora").value("2026-09-15T09:05:30"));
    }

    @Test
    void identificadorInvalidoResponde400JsonControlado() throws Exception {
        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(0, 2, "ENTRADA", null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
            .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    void tipoSalidaResponde400JsonControlado() throws Exception {
        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "SALIDA", null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void observacionLargaResponde400JsonControlado() throws Exception {
        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "ENTRADA", "x".repeat(201))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void referenciaInexistenteResponde404() throws Exception {
        when(asistenciaService.registrarEntrada(any()))
            .thenThrow(new RecursoNoEncontradoException(
                "La sesión indicada no existe."
            ));

        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(99, 2, "ENTRADA", null)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("REFERENCIA_NO_ENCONTRADA"));
    }

    @Test
    void duplicadoResponde409() throws Exception {
        when(asistenciaService.registrarEntrada(any()))
            .thenThrow(new RegistroDuplicadoException(
                "El participante ya tiene una entrada registrada en esta sesión."
            ));

        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "ENTRADA", null)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("REGISTRO_DUPLICADO"));
    }

    @Test
    void errorInesperadoResponde500SinDetallesInternos() throws Exception {
        when(asistenciaService.listarRegistros())
            .thenThrow(new IllegalStateException("detalle interno"));

        mockMvc.perform(get("/registros"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
            .andExpect(jsonPath("$.mensaje").value("Ocurrió un error interno."))
            .andExpect(content().string(org.hamcrest.Matchers.not(
                org.hamcrest.Matchers.containsString("detalle interno")
            )));
    }

    @Test
    void jsonConCampoDesconocidoResponde400() throws Exception {
        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idSesion\":1,\"idParticipante\":2,"
                    + "\"tipoRegistro\":\"ENTRADA\",\"extra\":true}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void postSinCredencialesResponde401ConDesafioJson() throws Exception {
        mockMvc.perform(post("/registros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "ENTRADA", null)))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string(
                "WWW-Authenticate",
                "Basic realm=\"PR06\""
            ))
            .andExpect(jsonPath("$.codigo").value("NO_AUTORIZADO"))
            .andExpect(jsonPath("$.mensaje")
                .value("Se requiere autenticación válida."));
    }

    @Test
    void credencialesIncorrectasResponden401() throws Exception {
        mockMvc.perform(post("/registros")
                .with(httpBasic(USUARIO, "incorrecta"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(1, 2, "ENTRADA", null)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.codigo").value("NO_AUTORIZADO"));
    }

    @Test
    void patchSinCredencialesResponde401() throws Exception {
        mockMvc.perform(patch("/sesiones/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_CURSO\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.codigo").value("NO_AUTORIZADO"));
    }

    @Test
    void patchValidoAutenticadoResponde200() throws Exception {
        when(asistenciaService.cambiarEstadoSesion(eq(1), any()))
            .thenReturn(sesionRespuesta("EN_CURSO"));

        mockMvc.perform(patch("/sesiones/1/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_CURSO\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idSesion").value(1))
            .andExpect(jsonPath("$.estado").value("EN_CURSO"));
    }

    @Test
    void patchConEstadoInvalidoResponde400() throws Exception {
        mockMvc.perform(patch("/sesiones/1/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"PAUSADA\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void patchConIdInvalidoResponde400() throws Exception {
        when(asistenciaService.cambiarEstadoSesion(eq(0), any()))
            .thenThrow(new com.pr06.asistencia.api.exception.DatosInvalidosException(
                "El identificador de sesión debe ser un entero positivo."
            ));

        mockMvc.perform(patch("/sesiones/0/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_CURSO\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void patchDeSesionInexistenteResponde404() throws Exception {
        when(asistenciaService.cambiarEstadoSesion(eq(99), any()))
            .thenThrow(new RecursoNoEncontradoException(
                "La sesión indicada no existe."
            ));

        mockMvc.perform(patch("/sesiones/99/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_CURSO\"}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo")
                .value("REFERENCIA_NO_ENCONTRADA"));
    }

    @Test
    void patchConTransicionProhibidaResponde409() throws Exception {
        when(asistenciaService.cambiarEstadoSesion(eq(1), any()))
            .thenThrow(new TransicionNoPermitidaException(
                "La transición de estado solicitada no está permitida."
            ));

        mockMvc.perform(patch("/sesiones/1/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"FINALIZADA\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo")
                .value("TRANSICION_NO_PERMITIDA"));
    }

    @Test
    void patchAlMismoEstadoResponde409() throws Exception {
        when(asistenciaService.cambiarEstadoSesion(eq(1), any()))
            .thenThrow(new TransicionNoPermitidaException(
                "La sesión ya se encuentra en el estado solicitado."
            ));

        mockMvc.perform(patch("/sesiones/1/estado")
                .with(httpBasic(USUARIO, CLAVE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"PROGRAMADA\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo")
                .value("TRANSICION_NO_PERMITIDA"))
            .andExpect(jsonPath("$.mensaje")
                .value("La sesión ya se encuentra en el estado solicitado."));
    }

    private RegistroDto registroRespuesta() {
        return new RegistroDto(
            9,
            1,
            2,
            "Sesión",
            "Ana López",
            "ENTRADA",
            LocalDateTime.of(2026, 9, 15, 9, 5, 30),
            "Evidencia"
        );
    }

    private SesionDto sesionRespuesta(String estado) {
        return new SesionDto(
            1,
            "Sesión",
            LocalDate.of(2026, 9, 15),
            LocalTime.of(9, 0),
            LocalTime.of(11, 0),
            estado,
            null
        );
    }

    private String solicitud(
            int idSesion,
            int idParticipante,
            String tipo,
            String observacion) {
        String valorObservacion = observacion == null
            ? "null"
            : "\"" + observacion + "\"";
        return "{\"idSesion\":" + idSesion
            + ",\"idParticipante\":" + idParticipante
            + ",\"tipoRegistro\":\"" + tipo + "\""
            + ",\"observacion\":" + valorObservacion + "}";
    }
}
