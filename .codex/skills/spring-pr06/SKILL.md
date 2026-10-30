---
name: spring-pr06
description: "Implementa o revisa exclusivamente la API P05 bajo api-spring/** con Java 11, Spring Boot 2.7.18 y el esquema PostgreSQL existente; no se aplica a pr06_p02/**."
---

# Spring Boot P05

Aplica esta skill sólo a tareas dentro de `api-spring/**`.

## Compatibilidad y arquitectura

- Usa Java 11 y Spring Boot 2.7.18.
- Organiza la aplicación por responsabilidades: Controller, Service, Repository y Entity.
- Expón DTOs como clases Java; no uses Records, porque no son compatibles con Java 11.
- No expongas entidades JPA directamente en los controladores REST.
- Usa `javax.persistence` y `javax.validation`, compatibles con Spring Boot 2.7.18; no uses los paquetes `jakarta.*` equivalentes de Spring Boot 3.
- Aplica validación de entrada y respuestas HTTP 2xx/4xx coherentes, con manejo controlado de errores que no revele SQL, trazas ni secretos.
- Mantén Lombok como opcional. No agregues la dependencia sólo para reducir código repetitivo sin una razón educativa o autorización explícita.

## Persistencia

- Mapea JPA a las tablas, columnas, claves y restricciones PostgreSQL existentes.
- Para claves `SERIAL`, usa una estrategia de generación compatible, como `GenerationType.IDENTITY`.
- No permitas que JPA cree o modifique automáticamente el esquema existente.
- Conserva la restricción de unicidad como protección final contra registros duplicados y traduce su violación a una respuesta controlada.
- Usa paginación sólo cuando el volumen o el contrato la justifiquen.

## Límites

- No modifiques `pr06_p02/**`, `schema.sql`, `data.sql` ni PostgreSQL sin autorización explícita.
- No crees ni actualices el backend fuera de `api-spring/**`.
- Conserva el alcance funcional heredado y el contrato aprobado; no inventes endpoints o transiciones de estado.
- Verifica las pruebas disponibles del backend y reporta con precisión lo no ejecutado.
