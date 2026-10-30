# Evidencias extremo a extremo — Fase 5A

Ejecución realizada el 28 de octubre de 2026 con Angular 16.2.12, Node.js
18.20.8, Spring Boot 2.7.18, Java 11 y PostgreSQL. El navegador utilizado fue
Google Chrome for Testing 156.0.8078.73 en modo headless; es una instancia real
de Chrome controlada mediante DevTools Protocol.

La identidad de PostgreSQL se comprobó antes de escribir:
`pr06_p05_test_20261026`. La base histórica `pr06_p02` no se utilizó en las
operaciones E2E.

## Resultado del recorrido

| Prueba | Resultado | Evidencia |
| --- | --- | --- |
| Salud de API y PostgreSQL | HTTP 200; `OK` y `DISPONIBLE` | `02-http-saneado.json`, `03-navegador-salud.png` |
| Consulta de sesiones | HTTP 200; 2 sesiones | `02-http-saneado.json`, `04-navegador-sesiones-antes.png` |
| Participantes activos | HTTP 200; 4 participantes | `02-http-saneado.json` |
| ENTRADA sesión 2 / participante 2 | HTTP 201; registro 3 | `12-http-primer-recorrido.txt`, `08-navegador-registros-persistidos.png`, `11-sql-despues.txt` |
| Consulta del registro persistido | HTTP 200; visible en Angular y SQL | `02-http-saneado.json`, `08-navegador-registros-persistidos.png`, `11-sql-despues.txt` |
| Repetición de la misma entrada | HTTP 409; no creó otra fila | `06-navegador-duplicado-409.png`, `11-sql-despues.txt`, `12-http-primer-recorrido.txt` |
| POST sin autenticación | HTTP 401; JSON controlado; no creó fila | `02-http-saneado.json`, `07-navegador-sin-autenticacion-401.png`, `11-sql-despues.txt` |
| EN_CURSO → FINALIZADA, sesión 1 | HTTP 200 | `02-http-saneado.json`, `09-navegador-patch-200.png`, `11-sql-despues.txt` |
| FINALIZADA → EN_CURSO | HTTP 409; permaneció FINALIZADA | `02-http-saneado.json`, `10-navegador-transicion-prohibida-409.png`, `11-sql-despues.txt` |

El estado pasó de 2 a 3 registros. Existe exactamente una ENTRADA para la
pareja sesión 2/participante 2 y ninguna para la pareja usada en el intento sin
autenticación, sesión 2/participante 3.

La captura inmediata del HTTP 201 se retiró porque mostraba el nombre de usuario
Basic efímero. El resultado se conserva sin credenciales mediante la respuesta
HTTP observada, la consulta Angular posterior y las consultas SQL.

## Relación con R05

| Criterio R05 | Evidencia |
| --- | --- |
| API REST de salud, consultas y cambio de estado | `02-http-saneado.json`, capturas 03, 04, 09 y 10 |
| Respuestas HTTP 2xx/4xx coherentes | `02-http-saneado.json`, `12-http-primer-recorrido.txt` |
| Validación y errores controlados | Respuestas 401 y 409 sin trazas ni SQL en `02-http-saneado.json` |
| Integración Angular–Spring Boot–PostgreSQL | Capturas 03, 06, 07, 08, 09 y 10 |
| Persistencia reproducible | `01-sql-antes.txt` y `11-sql-despues.txt` |
| Contrato OpenAPI | `../openapi.yaml` y `../openapi.json` |
| Pruebas positivas y negativas | `13-verificaciones-builds.txt` y recorrido de esta carpeta |
| README y evidencia extremo a extremo | Este índice y los README de ambos módulos |

## Archivos

- `01-sql-antes.txt`: identidad y estado inicial en transacción de solo lectura.
- `02-http-saneado.json`: método, ruta, estado y cuerpo; no contiene encabezados
  `Authorization`.
- `02-http-error.txt`: error real del intento intermedio bloqueado por el desafío
  HTTP Basic.
- `03`–`10`: capturas reales saneadas del navegador. La numeración 05 queda
  reservada para la captura retirada por seguridad.
- `11-sql-despues.txt`: estado final y comprobación de operaciones rechazadas.
- `12-http-primer-recorrido.txt`: recuperación saneada de HTTP 201 y 409.
- `13-verificaciones-builds.txt`: versiones, suites y builds ejecutados.
- `INCIDENCIAS.md`: fallos reales encontrados durante la automatización.
