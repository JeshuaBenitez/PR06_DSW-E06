package com.pr06.asistencia.api.repository;

import com.pr06.asistencia.api.entity.ParticipanteEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteRepository
        extends JpaRepository<ParticipanteEntity, Integer> {

    List<ParticipanteEntity> findByEstadoOrderByNombreAscApellidoAsc(
        String estado
    );
}
