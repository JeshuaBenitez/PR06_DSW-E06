package com.pr06.asistencia.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr06.asistencia.api.dto.ErrorDto;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String DESAFIO = "Basic realm=\"PR06\"";

    private final ObjectMapper objectMapper;

    public BasicAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, DESAFIO);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
            response.getOutputStream(),
            new ErrorDto(
                "NO_AUTORIZADO",
                "Se requiere autenticación válida."
            )
        );
    }
}
