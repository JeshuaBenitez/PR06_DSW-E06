package com.pr06.asistencia.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pr06.asistencia.api.config.BasicAuthenticationEntryPoint;
import com.pr06.asistencia.api.config.SecurityConfig;
import com.pr06.asistencia.api.service.HealthService;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
@Import({
    HealthService.class,
    SecurityConfig.class,
    BasicAuthenticationEntryPoint.class
})
@TestPropertySource(properties = {
    "pr06.security.username=operador-prueba",
    "pr06.security.password=clave-prueba-segura"
})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private Connection connection;

    @Test
    void devuelve200CuandoPostgresqlEstaDisponible() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.estado").value("OK"))
            .andExpect(jsonPath("$.baseDatos").value("DISPONIBLE"));
    }

    @Test
    void devuelve503CuandoFallaLaConexionConPostgresql() throws Exception {
        when(dataSource.getConnection()).thenThrow(
            new SQLException("Fallo simulado de conexión")
        );

        mockMvc.perform(get("/health"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.codigo").value("SERVICIO_NO_DISPONIBLE"))
            .andExpect(jsonPath("$.mensaje").value("La base de datos no está disponible."));
    }
}
