package com.pr06.asistencia.api.repository;

import com.pr06.asistencia.api.entity.RegistroEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegistroRepository extends JpaRepository<RegistroEntity, Integer> {

    @Query(
        "SELECT r FROM RegistroEntity r "
        + "JOIN FETCH r.sesion "
        + "JOIN FETCH r.participante "
        + "ORDER BY r.fechaHora DESC, r.idRegistro DESC"
    )
    List<RegistroEntity> findAllWithRelationsOrderByFechaHoraDesc();

    boolean existsBySesion_IdSesionAndParticipante_IdParticipanteAndTipoRegistro(
        Integer idSesion,
        Integer idParticipante,
        String tipoRegistro
    );
}
