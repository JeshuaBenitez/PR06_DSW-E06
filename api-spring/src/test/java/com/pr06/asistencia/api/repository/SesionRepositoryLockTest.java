package com.pr06.asistencia.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import javax.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

class SesionRepositoryLockTest {

    @Test
    void cambioDeEstadoDeclaraBloqueoPessimistaDeEscritura()
            throws NoSuchMethodException {
        Method metodo = SesionRepository.class.getMethod(
            "findByIdForUpdate",
            Integer.class
        );

        Lock bloqueo = metodo.getAnnotation(Lock.class);

        assertThat(bloqueo).isNotNull();
        assertThat(bloqueo.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}
