package com.pr06.asistencia.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.Assert;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BasicAuthenticationEntryPoint authenticationEntryPoint)
            throws Exception {
        http
            .csrf()
                .ignoringAntMatchers("/registros", "/sesiones/*/estado")
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers(
                    HttpMethod.GET,
                    "/health",
                    "/sesiones",
                    "/participantes",
                    "/registros"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/registros").authenticated()
                .antMatchers(
                    HttpMethod.PATCH,
                    "/sesiones/*/estado"
                ).authenticated()
                .anyRequest().denyAll()
                .and()
            .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${pr06.security.username}") String username,
            @Value("${pr06.security.password}") String password,
            PasswordEncoder passwordEncoder) {
        Assert.hasText(username, "API_BASIC_USER debe estar configurado");
        Assert.hasText(password, "API_BASIC_PASSWORD debe estar configurado");
        UserDetails user = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("OPERADOR")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
