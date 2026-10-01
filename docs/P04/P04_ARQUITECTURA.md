# P04 — Arquitectura objetivo

Fuente principal: [docs/P04/arquitectura_pr06.puml](arquitectura_pr06.puml).

La arquitectura Web 3.0 solicitada es un diseño pendiente de implementación. La aplicación existente utiliza JSF/PrimeFaces y DAO JDBC; este diagrama no describe un despliegue ya migrado.

## Responsabilidades y flujo

1. El usuario interactúa con Angular 16 en el navegador.
2. Angular envía GET para consultas y POST con JSON para registrar una entrada.
3. La API REST de Spring Boot 2.7.18 / Java 11 valida el formato en Controller y las reglas de negocio en Service.
4. Repository/JPA consulta o persiste en PostgreSQL 16, versión objetivo.
5. El resultado regresa por Repository → Service → Controller → respuesta HTTP/JSON → Angular → usuario.

Las consultas devuelven 200 y arreglos JSON, incluidos arreglos vacíos. Una inserción confirmada devuelve 201 y el registro persistido. Los errores previstos son 400 (formato, participante inactivo o validación), 404 (sesión o participante inexistente), 409 (duplicado) y 500 (fallo interno). La respuesta de error utiliza un formato JSON común y no expone SQL, credenciales ni trazas.

La validación en cliente mejora la interacción; el servidor debe repetirla. La comprobación previa de duplicados no reemplaza la restricción UNIQUE de PostgreSQL. Los fallos por concurrencia deben traducirse a 409.

## Componentes y microcomponentes Angular propuestos

Los nombres siguientes son decisiones de diseño; no existen todavía archivos TypeScript. Los microcomponentes son componentes Angular pequeños y reutilizables, no microfrontends.

| Componente de página | Composición |
|---|---|
| InicioComponent | Presentación y NavegacionComponent |
| SesionesComponent | NavegacionComponent, MensajesComponent y TablaSesionesComponent |
| RegistroComponent | NavegacionComponent, MensajesComponent, FormularioEntradaComponent y TablaRegistrosComponent |
| RegistrosComponent | NavegacionComponent, MensajesComponent y TablaRegistrosComponent |

Las páginas orquestan las consultas y acciones mediante `AsistenciaApiService` y `HttpClient`. Los microcomponentes reciben datos mediante `@Input` y emiten acciones mediante `@Output`: enviar formulario o solicitar actualización. `MensajesComponent` recibe resultados y errores. El formulario recibe las sesiones y participantes activos; no añade otra página de participantes.

Después de registrar una entrada, la página actualiza los mensajes y vuelve a consultar los registros para actualizar la tabla. Los microcomponentes no realizan persistencia ni llamadas directas a PostgreSQL.

## DTOs y entities en Spring Boot

| Tipo propuesto | Función | Esquema OpenAPI / tabla |
|---|---|---|
| RegistroEntradaDTO | Solicitud de registro; IDs, tipo y observación | RegistroEntrada |
| SesionDTO | Respuesta de consulta de sesiones | Sesion |
| ParticipanteDTO | Respuesta de consulta de participantes activos | Participante |
| RegistroDTO | Respuesta de consulta o creación, incluidos nombres relacionados | Registro |
| ErrorDTO | Código y mensaje de error | Error |
| SesionEntity | Persistencia JPA | sesion |
| ParticipanteEntity | Persistencia JPA | participante |
| RegistroEntity | Persistencia JPA | registro |
| IncidenciaEntity | Mapeo previsto del esquema existente | incidencia |
| DispositivoEntity | Mapeo previsto del esquema existente | dispositivo |
| EventoEntity | Mapeo previsto del esquema existente | evento |

Controller recibe y devuelve DTOs serializados como JSON. Service aplica las reglas y convierte entre DTOs y entities. Repository/JPA opera con entities que representan las tablas. Las entities no se serializan directamente como respuestas HTTP. El mapeo puede realizarse explícitamente en Service; no requiere agregar una biblioteca.

`RegistroDTO.nombreSesion` y `nombreParticipante` son datos de presentación derivados de relaciones, no columnas nuevas de RegistroEntity. Los IDs de RegistroEntradaDTO se resuelven a sesión y participante existentes. ErrorDTO no tiene entity ni tabla. Las entities de incidencia, dispositivo y evento documentan el mapeo previsto; no introducen endpoints ni repositorios adicionales en el alcance funcional actual.

## Contrato propuesto

[openapi.yaml](openapi.yaml) y [openapi.json](openapi.json) describen el mismo contrato OpenAPI 3.0.3. El servidor `http://localhost:8080/api` es propuesto, distinto del contexto actual `/pr06_p02`. No se establece una nueva implementación mediante esta documentación.

Se conserva el alcance funcional actual: consultar sesiones, participantes activos y registros; registrar ENTRADA. No se incorporan autenticación, colas, microservicios ni operaciones adicionales.

## Validación de la fuente

El diagrama se valida y compila con PlantUML. Las imágenes de comprobación se generan fuera del repositorio; el `.puml` se conserva como fuente principal.
