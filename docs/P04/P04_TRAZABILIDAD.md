# P04 — Trazabilidad

| Requisito | Evidencia actual | Entregable / estado |
|---|---|---|
| Cliente Angular 16 | No existe cliente Angular | Arquitectura objetivo; implementación PENDIENTE |
| API Spring Boot 2.7.18 / Java 11 | POM actual compila Java 11 y utiliza JSF/Servlet | Controller, Service y Repository/JPA PENDIENTES |
| Páginas y microcomponentes Angular | Vistas XHTML actuales; no componentes Angular | Composición y eventos propuestos en arquitectura; PENDIENTE |
| DTOs HTTP | Esquemas OpenAPI propuestos | RegistroEntradaDTO, SesionDTO, ParticipanteDTO, RegistroDTO y ErrorDTO; PENDIENTE |
| Entities JPA | Seis tablas SQL; modelos Java actuales sin JPA | Seis mapeos Entity/tabla propuestos en ambos diagramas; PENDIENTE |
| Conversión DTO/entity | No existe Service Spring | Responsabilidad propuesta de Service; PENDIENTE |
| HTTP GET/POST y JSON | Flujo actual JSF y DAO | Contrato propuesto en openapi.yaml y openapi.json |
| Consulta de sesiones | SesionDAO.obtenerTodas | GET /sesiones propuesto |
| Consulta de participantes activos | ParticipanteDAO.obtenerActivos | GET /participantes propuesto |
| Consulta de registros | RegistroDAO.listarRegistros | GET /registros propuesto |
| Registro de ENTRADA | RegistroBean.registrar y RegistroDAO.insertarRegistro | POST /registros propuesto |
| Validación y errores | RegistroBean, validadores JSF y manejo de SQLException | Respuestas REST 400/404/409/500 propuestas |
| Éxito REST 2xx | Mensaje de éxito JSF actual | Respuestas REST 200/201 propuestas |
| Prevención de duplicados | schema.sql UNIQUE y RegistroDAO.existeRegistro | Restricción documentada; futura traducción HTTP 409 |
| Seis entidades relacionales | schema.sql y data.sql | modelo_datos_pr06.puml; cobertura por capa en P04_MODELO_DATOS.md |
| Arquitectura y flujo de regreso | Diseño solicitado por el usuario | arquitectura_pr06.puml |
| PostgreSQL 16 | Scripts PostgreSQL; versión instalada no comprobada | Versión objetivo; verificación del entorno PENDIENTE |

Los archivos fuente están en [arquitectura](arquitectura_pr06.puml) y [modelo de datos](modelo_datos_pr06.puml). Ver [diagnóstico](P04_DIAGNOSTICO.md), [arquitectura](P04_ARQUITECTURA.md) y [modelo](P04_MODELO_DATOS.md).

Las rutas indicadas son relativas al servidor propuesto `http://localhost:8080/api`. No se declaran implementadas ni probadas mediante llamadas HTTP. Esta entrega valida documentación y diagramas, no una API ejecutable.
