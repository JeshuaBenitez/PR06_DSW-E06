# Instrucciones del repositorio PR06

## Contexto y alcance

PR06 es un proyecto académico de Desarrollo de Sistemas Web que evoluciona sin reemplazar los incrementos anteriores:

- P02: JSP, Servlets, JDBC y PostgreSQL.
- P03: JSF 2.3, PrimeFaces y PostgreSQL.
- P05: integración nueva mediante Angular y Spring Boot, conservando los requisitos funcionales y la evidencia anteriores.

El perfil tecnológico de P05 es Java 11, Spring Boot 2.7.18, Angular 16.2.12 y PostgreSQL. Verifica las versiones declaradas y disponibles antes de afirmar compatibilidad.

## Límites de modificación

- Trata `pr06_p02/**` como solo lectura. No edites, muevas, elimines, renombres, reformatees ni actualices dependencias dentro de esa ruta sin autorización explícita que la mencione.
- Desarrolla el frontend P05 únicamente en `frontend-angular/**` y el backend P05 únicamente en `api-spring/**`.
- Si una tarea P05 parece requerir cambios en `pr06_p02/**`, detente y explica la dependencia antes de modificarlo.
- No modifiques `schema.sql`, `data.sql`, tablas, restricciones ni datos de PostgreSQL sin autorización explícita.
- No ejecutes scripts de inicialización contra una base existente salvo petición explícita y después de identificar el destino con seguridad.
- Conserva la documentación, los diagramas, el contrato OpenAPI y las evidencias históricas salvo que la tarea solicite expresamente actualizarlos.

## Seguridad

- No muestres, copies ni registres contraseñas, tokens, cadenas de conexión con credenciales u otros secretos.
- Usa configuración externa y ejemplos sin valores reales cuando una tarea autorizada requiera configurar credenciales.
- Si encuentras un secreto, informa su ubicación y riesgo sin reproducir su valor.

## Skills del proyecto

- Usa `angular-pr06` para implementar o revisar trabajo dentro de `frontend-angular/**`.
- Usa `spring-pr06` para implementar o revisar trabajo dentro de `api-spring/**`.
- Usa `concise-reporting` al informar cambios, verificaciones, errores y pendientes.
- No apliques las skills Angular o Spring al código heredado de `pr06_p02/**`.

## Pruebas y evidencia

- Preserva las pruebas, capturas y evidencia existentes.
- Ejecuta verificaciones proporcionales al cambio cuando la tarea las autorice y no requieran efectos persistentes no solicitados.
- No presentes una compilación, prueba, integración o despliegue como exitoso si no se ejecutó en el estado actual.
- Informa los comandos ejecutados, su resultado y cualquier prueba omitida.
- Usa estos estados de forma consistente:
  - `VERIFICADO`: se comprobó durante la tarea y existe evidencia observable.
  - `NO_VERIFICADO`: se intentó comprobar, pero no se confirmó por fallo, limitación o resultado inconcluso.
  - `PENDIENTE`: todavía no se implementó o no se ejecutó la comprobación.
