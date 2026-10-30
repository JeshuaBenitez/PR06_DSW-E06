# P05 — Decisiones técnicas del contrato REST

- `openapi.yaml` es la fuente principal. `openapi.json` se genera mecánicamente desde el YAML y no debe editarse por separado.
- El prefijo común es `/api`; las rutas declaradas en OpenAPI son relativas a `http://localhost:8080/api`.
- Los GET son públicos. `POST /registros` y `PATCH /sesiones/{idSesion}/estado` usan HTTP Basic con credenciales configuradas externamente.
- P05 registra únicamente `ENTRADA`. La sesión y el participante deben existir, el participante debe estar `ACTIVO`, los IDs deben ser positivos y la observación admite hasta 200 caracteres.
- El estado de una sesión puede ser `PROGRAMADA`, `EN_CURSO`, `FINALIZADA` o `CANCELADA`. Sólo se permiten `PROGRAMADA → EN_CURSO`, `PROGRAMADA → CANCELADA` y `EN_CURSO → FINALIZADA`.
- El registro de ENTRADA no depende del estado de la sesión, para conservar el comportamiento de P02/P03.
- Los errores usan `codigo` y `mensaje` y no exponen SQL, trazas, credenciales ni detalles internos.
- No se modifica el esquema PostgreSQL. Como `sesion.estado` no tiene `NOT NULL` ni `CHECK`, la aplicación P05 debe garantizar los estados del contrato y tratar datos heredados no reconocidos como una inconsistencia controlada.
- `fechaHora` representa la hora local de la columna PostgreSQL `TIMESTAMP`, sin zona horaria.
- CORS se configurará en Spring Boot para el origen local de Angular; CORS no sustituye la autenticación.
- Los contratos de `docs/P04/` permanecen como evidencia histórica y no deben sobrescribirse.
