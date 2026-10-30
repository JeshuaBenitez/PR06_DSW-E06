package com.pr06.asistencia.api.service;

import com.pr06.asistencia.api.dto.ParticipanteDto;
import com.pr06.asistencia.api.dto.RegistroDto;
import com.pr06.asistencia.api.dto.RegistroEntradaDto;
import com.pr06.asistencia.api.dto.CambioEstadoSesionDto;
import com.pr06.asistencia.api.dto.SesionDto;
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
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsistenciaService {

    private static final String ACTIVO = "ACTIVO";
    private static final String ENTRADA = "ENTRADA";
    private static final String MENSAJE_DUPLICADO =
        "El participante ya tiene una entrada registrada en esta sesión.";

    private final SesionRepository sesionRepository;
    private final ParticipanteRepository participanteRepository;
    private final RegistroRepository registroRepository;

    public AsistenciaService(
            SesionRepository sesionRepository,
            ParticipanteRepository participanteRepository,
            RegistroRepository registroRepository) {
        this.sesionRepository = sesionRepository;
        this.participanteRepository = participanteRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public List<SesionDto> listarSesiones() {
        return sesionRepository.findAllByOrderByFechaAscHoraInicioAsc()
            .stream()
            .map(this::convertirSesion)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipanteDto> listarParticipantesActivos() {
        return participanteRepository
            .findByEstadoOrderByNombreAscApellidoAsc(ACTIVO)
            .stream()
            .map(this::convertirParticipante)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroDto> listarRegistros() {
        return registroRepository.findAllWithRelationsOrderByFechaHoraDesc()
            .stream()
            .map(this::convertirRegistro)
            .collect(Collectors.toList());
    }

    @Transactional
    public SesionDto cambiarEstadoSesion(
            Integer idSesion,
            CambioEstadoSesionDto solicitud) {
        validarCambioEstado(idSesion, solicitud);

        SesionEntity sesion = sesionRepository.findByIdForUpdate(idSesion)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "La sesión indicada no existe."
            ));

        String estadoActual = sesion.getEstado();
        String nuevoEstado = solicitud.getEstado();
        if (nuevoEstado.equals(estadoActual)) {
            throw new TransicionNoPermitidaException(
                "La sesión ya se encuentra en el estado solicitado."
            );
        }
        if (!transicionPermitida(estadoActual, nuevoEstado)) {
            throw new TransicionNoPermitidaException(
                "La transición de estado solicitada no está permitida."
            );
        }

        sesion.cambiarEstado(nuevoEstado);
        return convertirSesion(sesion);
    }

    @Transactional
    public RegistroDto registrarEntrada(RegistroEntradaDto solicitud) {
        validarSolicitud(solicitud);

        SesionEntity sesion = sesionRepository.findById(solicitud.getIdSesion())
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "La sesión indicada no existe."
            ));

        ParticipanteEntity participante = participanteRepository
            .findById(solicitud.getIdParticipante())
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "El participante indicado no existe."
            ));

        if (!ACTIVO.equals(participante.getEstado())) {
            throw new DatosInvalidosException(
                "El participante debe estar activo."
            );
        }

        if (registroRepository
                .existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
                    sesion.getIdSesion(),
                    participante.getIdParticipante(),
                    ENTRADA
                )) {
            throw new RegistroDuplicadoException(MENSAJE_DUPLICADO);
        }

        String observacion = limpiarObservacion(solicitud.getObservacion());
        RegistroEntity registro = new RegistroEntity(
            sesion,
            participante,
            ENTRADA,
            observacion
        );

        try {
            return convertirRegistro(registroRepository.saveAndFlush(registro));
        } catch (DataIntegrityViolationException exception) {
            if (esViolacionUnica(exception)) {
                throw new RegistroDuplicadoException(MENSAJE_DUPLICADO);
            }
            throw exception;
        }
    }

    private void validarSolicitud(RegistroEntradaDto solicitud) {
        if (solicitud == null
                || solicitud.getIdSesion() == null
                || solicitud.getIdSesion() <= 0
                || solicitud.getIdParticipante() == null
                || solicitud.getIdParticipante() <= 0) {
            throw new DatosInvalidosException(
                "Los identificadores deben ser enteros positivos."
            );
        }
        if (!ENTRADA.equals(solicitud.getTipoRegistro())) {
            throw new DatosInvalidosException(
                "El tipo de registro debe ser ENTRADA."
            );
        }
        if (solicitud.getObservacion() != null
                && solicitud.getObservacion().length() > 200) {
            throw new DatosInvalidosException(
                "La observación no puede exceder 200 caracteres."
            );
        }
    }

    private void validarCambioEstado(
            Integer idSesion,
            CambioEstadoSesionDto solicitud) {
        if (idSesion == null || idSesion <= 0) {
            throw new DatosInvalidosException(
                "El identificador de sesión debe ser un entero positivo."
            );
        }
        if (solicitud == null || !esEstadoValido(solicitud.getEstado())) {
            throw new DatosInvalidosException(
                "El estado solicitado no es válido."
            );
        }
    }

    private boolean esEstadoValido(String estado) {
        return "PROGRAMADA".equals(estado)
            || "EN_CURSO".equals(estado)
            || "FINALIZADA".equals(estado)
            || "CANCELADA".equals(estado);
    }

    private boolean transicionPermitida(String actual, String nuevoEstado) {
        return ("PROGRAMADA".equals(actual)
                && ("EN_CURSO".equals(nuevoEstado)
                    || "CANCELADA".equals(nuevoEstado)))
            || ("EN_CURSO".equals(actual)
                && "FINALIZADA".equals(nuevoEstado));
    }

    private String limpiarObservacion(String observacion) {
        if (observacion == null) {
            return null;
        }
        String limpia = observacion.trim();
        return limpia.isEmpty() ? null : limpia;
    }

    private boolean esViolacionUnica(Throwable exception) {
        Throwable actual = exception;
        while (actual != null) {
            if (actual instanceof SQLException
                    && "23505".equals(((SQLException) actual).getSQLState())) {
                return true;
            }
            actual = actual.getCause();
        }
        return false;
    }

    private SesionDto convertirSesion(SesionEntity sesion) {
        return new SesionDto(
            sesion.getIdSesion(),
            sesion.getNombre(),
            sesion.getFecha(),
            sesion.getHoraInicio(),
            sesion.getHoraFin(),
            sesion.getEstado(),
            sesion.getDescripcion()
        );
    }

    private ParticipanteDto convertirParticipante(
            ParticipanteEntity participante) {
        return new ParticipanteDto(
            participante.getIdParticipante(),
            participante.getCodigoFicticio(),
            participante.getNombre(),
            participante.getApellido(),
            participante.getEstado()
        );
    }

    private RegistroDto convertirRegistro(RegistroEntity registro) {
        ParticipanteEntity participante = registro.getParticipante();
        return new RegistroDto(
            registro.getIdRegistro(),
            registro.getSesion().getIdSesion(),
            participante.getIdParticipante(),
            registro.getSesion().getNombre(),
            participante.getNombre() + " " + participante.getApellido(),
            registro.getTipoRegistro(),
            registro.getFechaHora(),
            registro.getObservacion()
        );
    }
}
