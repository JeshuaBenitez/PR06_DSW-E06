package com.pr06.asistencia.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pr06.asistencia.api.dto.CambioEstadoSesionDto;
import com.pr06.asistencia.api.dto.RegistroDto;
import com.pr06.asistencia.api.dto.RegistroEntradaDto;
import com.pr06.asistencia.api.entity.ParticipanteEntity;
import com.pr06.asistencia.api.entity.RegistroEntity;
import com.pr06.asistencia.api.entity.SesionEntity;
import com.pr06.asistencia.api.exception.DatosInvalidosException;
import com.pr06.asistencia.api.exception.RecursoNoEncontradoException;
import com.pr06.asistencia.api.exception.RegistroDuplicadoException;
import com.pr06.asistencia.api.exception.TransicionNoPermitidaException;
import com.pr06.asistencia.api.repository.ParticipanteRepository;
import com.pr06.asistencia.api.repository.RegistroRepository;
import com.pr06.asistencia.api.repository.SesionRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private RegistroRepository registroRepository;

    private AsistenciaService service;
    private SesionEntity sesion;
    private ParticipanteEntity participante;

    @BeforeEach
    void preparar() {
        service = new AsistenciaService(
            sesionRepository,
            participanteRepository,
            registroRepository
        );
        sesion = sesion(1);
        participante = participante(2, "ACTIVO");
    }

    @Test
    void listaConsultasYConvierteEntidadesEnDtos() {
        RegistroEntity registro = registro(3, sesion, participante, " Nota ");
        when(sesionRepository.findAllByOrderByFechaAscHoraInicioAsc())
            .thenReturn(Collections.singletonList(sesion));
        when(participanteRepository.findByEstadoOrderByNombreAscApellidoAsc("ACTIVO"))
            .thenReturn(Collections.singletonList(participante));
        when(registroRepository.findAllWithRelationsOrderByFechaHoraDesc())
            .thenReturn(Collections.singletonList(registro));

        assertThat(service.listarSesiones()).singleElement()
            .satisfies(dto -> assertThat(dto.getIdSesion()).isEqualTo(1));
        assertThat(service.listarParticipantesActivos()).singleElement()
            .satisfies(dto -> {
                assertThat(dto.getIdParticipante()).isEqualTo(2);
                assertThat(dto.getEstado()).isEqualTo("ACTIVO");
            });
        assertThat(service.listarRegistros()).singleElement()
            .satisfies(dto -> {
                assertThat(dto.getNombreSesion()).isEqualTo("Sesión de prueba");
                assertThat(dto.getNombreParticipante()).isEqualTo("Ana López");
            });
    }

    @Test
    void devuelveListasVacias() {
        when(sesionRepository.findAllByOrderByFechaAscHoraInicioAsc())
            .thenReturn(Collections.emptyList());
        when(participanteRepository.findByEstadoOrderByNombreAscApellidoAsc("ACTIVO"))
            .thenReturn(Collections.emptyList());
        when(registroRepository.findAllWithRelationsOrderByFechaHoraDesc())
            .thenReturn(Collections.emptyList());

        assertThat(service.listarSesiones()).isEmpty();
        assertThat(service.listarParticipantesActivos()).isEmpty();
        assertThat(service.listarRegistros()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
        "PROGRAMADA, EN_CURSO",
        "PROGRAMADA, CANCELADA",
        "EN_CURSO, FINALIZADA"
    })
    void cambiaEstadoEnTransicionesPermitidas(
            String estadoActual,
            String nuevoEstado) {
        SesionEntity bloqueada = sesion(1, estadoActual);
        when(sesionRepository.findByIdForUpdate(1))
            .thenReturn(Optional.of(bloqueada));

        assertThat(service.cambiarEstadoSesion(
            1,
            new CambioEstadoSesionDto(nuevoEstado)
        ).getEstado()).isEqualTo(nuevoEstado);

        verify(sesionRepository).findByIdForUpdate(1);
    }

    @Test
    void rechazaIdDeSesionInvalidoAlCambiarEstado() {
        assertThatThrownBy(() -> service.cambiarEstadoSesion(
            0,
            new CambioEstadoSesionDto("EN_CURSO")
        )).isInstanceOf(DatosInvalidosException.class);

        verify(sesionRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void rechazaEstadoDesconocido() {
        assertThatThrownBy(() -> service.cambiarEstadoSesion(
            1,
            new CambioEstadoSesionDto("PAUSADA")
        )).isInstanceOf(DatosInvalidosException.class)
          .hasMessage("El estado solicitado no es válido.");
    }

    @Test
    void rechazaSesionInexistenteAlCambiarEstado() {
        when(sesionRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cambiarEstadoSesion(
            99,
            new CambioEstadoSesionDto("EN_CURSO")
        )).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void rechazaCambioAlMismoEstado() {
        when(sesionRepository.findByIdForUpdate(1))
            .thenReturn(Optional.of(sesion(1, "PROGRAMADA")));

        assertThatThrownBy(() -> service.cambiarEstadoSesion(
            1,
            new CambioEstadoSesionDto("PROGRAMADA")
        )).isInstanceOf(TransicionNoPermitidaException.class)
          .hasMessage("La sesión ya se encuentra en el estado solicitado.");
    }

    @Test
    void rechazaRetrocesoOTransicionNoPermitida() {
        when(sesionRepository.findByIdForUpdate(1))
            .thenReturn(Optional.of(sesion(1, "FINALIZADA")));

        assertThatThrownBy(() -> service.cambiarEstadoSesion(
            1,
            new CambioEstadoSesionDto("EN_CURSO")
        )).isInstanceOf(TransicionNoPermitidaException.class)
          .hasMessage("La transición de estado solicitada no está permitida.");
    }

    @Test
    void registraEntradaValidaYRecortaObservacion() {
        prepararReferenciasExistentes();
        when(registroRepository
                .existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
                    1, 2, "ENTRADA"))
            .thenReturn(false);
        when(registroRepository.saveAndFlush(any(RegistroEntity.class)))
            .thenAnswer(invocation -> {
                RegistroEntity guardado = invocation.getArgument(0);
                ReflectionTestUtils.setField(guardado, "idRegistro", 9);
                ReflectionTestUtils.setField(
                    guardado,
                    "fechaHora",
                    LocalDateTime.of(2026, 9, 15, 9, 5)
                );
                return guardado;
            });

        RegistroDto resultado = service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", "  Evidencia  ")
        );

        assertThat(resultado.getIdRegistro()).isEqualTo(9);
        assertThat(resultado.getTipoRegistro()).isEqualTo("ENTRADA");
        assertThat(resultado.getObservacion()).isEqualTo("Evidencia");
    }

    @Test
    void convierteObservacionVaciaEnNull() {
        prepararReferenciasExistentes();
        when(registroRepository
                .existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
                    1, 2, "ENTRADA"))
            .thenReturn(false);
        when(registroRepository.saveAndFlush(any(RegistroEntity.class)))
            .thenAnswer(invocation -> {
                RegistroEntity guardado = invocation.getArgument(0);
                ReflectionTestUtils.setField(guardado, "idRegistro", 10);
                ReflectionTestUtils.setField(guardado, "fechaHora", LocalDateTime.now());
                return guardado;
            });

        RegistroDto resultado = service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", "   ")
        );

        assertThat(resultado.getObservacion()).isNull();
    }

    @Test
    void rechazaIdentificadoresNoPositivos() {
        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(0, -2, "ENTRADA", null)
        )).isInstanceOf(DatosInvalidosException.class)
          .hasMessage("Los identificadores deben ser enteros positivos.");

        verify(registroRepository, never()).saveAndFlush(any());
    }

    @Test
    void rechazaSesionInexistente() {
        when(sesionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(99, 2, "ENTRADA", null)
        )).isInstanceOf(RecursoNoEncontradoException.class)
          .hasMessage("La sesión indicada no existe.");
    }

    @Test
    void rechazaParticipanteInexistente() {
        when(sesionRepository.findById(1)).thenReturn(Optional.of(sesion));
        when(participanteRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 99, "ENTRADA", null)
        )).isInstanceOf(RecursoNoEncontradoException.class)
          .hasMessage("El participante indicado no existe.");
    }

    @Test
    void rechazaParticipanteInactivo() {
        when(sesionRepository.findById(1)).thenReturn(Optional.of(sesion));
        when(participanteRepository.findById(2))
            .thenReturn(Optional.of(participante(2, "INACTIVO")));

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", null)
        )).isInstanceOf(DatosInvalidosException.class)
          .hasMessage("El participante debe estar activo.");
    }

    @Test
    void rechazaTipoSalida() {
        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "SALIDA", null)
        )).isInstanceOf(DatosInvalidosException.class)
          .hasMessage("El tipo de registro debe ser ENTRADA.");
    }

    @Test
    void validaLongitudAntesDeRecortarObservacion() {
        String observacion = " ".repeat(201);

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", observacion)
        )).isInstanceOf(DatosInvalidosException.class)
          .hasMessage("La observación no puede exceder 200 caracteres.");
    }

    @Test
    void rechazaDuplicadoDetectadoAntesDeInsertar() {
        prepararReferenciasExistentes();
        when(registroRepository
                .existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
                    1, 2, "ENTRADA"))
            .thenReturn(true);

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", null)
        )).isInstanceOf(RegistroDuplicadoException.class);

        verify(registroRepository, never()).saveAndFlush(any());
    }

    @Test
    void traduceRestriccionUnicaConcurrenteEnDuplicado() {
        prepararReferenciasExistentes();
        when(registroRepository
                .existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
                    1, 2, "ENTRADA"))
            .thenReturn(false);
        when(registroRepository.saveAndFlush(any(RegistroEntity.class)))
            .thenThrow(new DataIntegrityViolationException(
                "restricción",
                new SQLException("duplicado", "23505")
            ));

        assertThatThrownBy(() -> service.registrarEntrada(
            new RegistroEntradaDto(1, 2, "ENTRADA", null)
        )).isInstanceOf(RegistroDuplicadoException.class);
    }

    private void prepararReferenciasExistentes() {
        when(sesionRepository.findById(1)).thenReturn(Optional.of(sesion));
        when(participanteRepository.findById(2))
            .thenReturn(Optional.of(participante));
    }

    private SesionEntity sesion(int id) {
        return sesion(id, "PROGRAMADA");
    }

    private SesionEntity sesion(int id, String estado) {
        SesionEntity entidad = new SesionEntity(
            "Sesión de prueba",
            LocalDate.of(2026, 9, 15),
            LocalTime.of(9, 0),
            LocalTime.of(11, 0),
            estado,
            "Descripción"
        );
        ReflectionTestUtils.setField(entidad, "idSesion", id);
        return entidad;
    }

    private ParticipanteEntity participante(int id, String estado) {
        ParticipanteEntity entidad = new ParticipanteEntity(
            "P00" + id,
            "Ana",
            "López",
            estado
        );
        ReflectionTestUtils.setField(entidad, "idParticipante", id);
        return entidad;
    }

    private RegistroEntity registro(
            int id,
            SesionEntity unaSesion,
            ParticipanteEntity unParticipante,
            String observacion) {
        RegistroEntity entidad = new RegistroEntity(
            unaSesion,
            unParticipante,
            "ENTRADA",
            observacion
        );
        ReflectionTestUtils.setField(entidad, "idRegistro", id);
        ReflectionTestUtils.setField(
            entidad,
            "fechaHora",
            LocalDateTime.of(2026, 9, 15, 9, 5)
        );
        return entidad;
    }
}
