# P04 — Diagnóstico

Revisión del repositorio realizada el 29 de septiembre de 2026. Esta entrega contiene documentación y diseño objetivo; no implementa la migración.

## Estado actual

- `pr06_p02/pom.xml`: WAR `pr06_p02`, compilación Java 11, MyFaces 2.3.10, PrimeFaces 12.0.0 y controlador JDBC PostgreSQL 42.7.2.
- Interfaz JSF/PrimeFaces y vistas JSP/Servlet previas conservadas.
- Persistencia mediante `SesionDAO`, `ParticipanteDAO` y `RegistroDAO`; modelos Java sin anotaciones JPA.
- `RegistroBean` valida referencias, participante activo, ENTRADA, observación de hasta 200 caracteres y duplicados. El DAO comprueba existencia; PostgreSQL aplica UNIQUE incluso ante concurrencia.
- `schema.sql` define seis tablas. `data.sql` carga dos sesiones, cuatro participantes y un dispositivo; no carga registros, incidencias ni eventos.
- No se encontraron cliente Angular, Spring Boot, Controller REST, Service ni Repository/JPA. La versión del servidor PostgreSQL no se verifica a partir de los scripts.

## Objetivo solicitado

Angular 16 → HTTP/JSON → Spring Boot 2.7.18 / Java 11 → Controller → Service → Repository/JPA → PostgreSQL 16. Las versiones son objetivos del diseño, no una certificación del entorno instalado.

El contrato OpenAPI es una propuesta para consultar sesiones, participantes activos y registros, y registrar ENTRADA. Sus rutas todavía no están implementadas. No se añaden operaciones de SALIDA ni CRUD para otras tablas.

## Entregables

- [Arquitectura](P04_ARQUITECTURA.md)
- [Modelo de datos](P04_MODELO_DATOS.md)
- [Trazabilidad](P04_TRAZABILIDAD.md)
- Contrato propuesto: [YAML](openapi.yaml) y [JSON](openapi.json), equivalentes.

No se modifican código de aplicación, SQL, configuración de conexión ni credenciales.

## Ampliación del diseño — 30 de septiembre de 2026

Se incorporan al diseño páginas y microcomponentes Angular, AsistenciaApiService/HttpClient, DTOs HTTP y entities JPA. Todos estos elementos siguen PENDIENTES de implementación. La documentación de arquitectura define sus responsabilidades y correspondencia con el contrato OpenAPI y las seis tablas. No cambian las rutas, los campos del contrato ni el esquema SQL.
