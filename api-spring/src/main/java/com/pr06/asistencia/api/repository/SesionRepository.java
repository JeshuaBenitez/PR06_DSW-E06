package com.pr06.asistencia.api.repository;

import com.pr06.asistencia.api.entity.SesionEntity;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SesionRepository extends JpaRepository<SesionEntity, Integer> {

    List<SesionEntity> findAllByOrderByFechaAscHoraInicioAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SesionEntity s WHERE s.idSesion = :idSesion")
    Optional<SesionEntity> findByIdForUpdate(
        @Param("idSesion") Integer idSesion
    );
}
