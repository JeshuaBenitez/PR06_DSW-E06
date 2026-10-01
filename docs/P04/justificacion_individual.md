# P04 — Justificación individual

Base técnica para adaptar a la aportación personal del integrante. Este documento describe decisiones de diseño; no acredita una migración implementada ni actividades individuales no documentadas.

## Arquitectura propuesta

La separación entre Angular y una API REST permite definir el intercambio mediante HTTP y JSON. Las páginas coordinan el recorrido de consulta y registro; los microcomponentes reutilizan navegación, formularios, mensajes y tablas. El servicio HTTP concentra las llamadas al servidor.

En Spring Boot, Controller recibe solicitudes, Service aplica las reglas de negocio y Repository/JPA gestiona la persistencia. Los DTOs representan el contrato HTTP y las entities representan las tablas. Esta separación permite devolver datos de presentación sin agregar columnas al modelo relacional ni exponer directamente las entities.

## Integridad de los datos

El modelo conserva las seis tablas de schema.sql, sus tipos, claves y cardinalidades. La restricción UNIQUE sobre sesión, participante y tipo de registro evita duplicados incluso ante solicitudes simultáneas. La validación en la aplicación complementa esa protección, comprobando referencias, participante activo, tipo ENTRADA y longitud de la observación.

## Contrato y alcance

Los contratos YAML y JSON describen la misma propuesta de consultas y registro de entrada, con respuestas de éxito y errores diferenciados. No se añaden operaciones de SALIDA ni funcionalidades ajenas al recorrido actual. Angular, Spring Boot y JPA siguen pendientes de implementación; la aplicación existente utiliza JSF/PrimeFaces y DAO JDBC.

## Fuentes del diseño

- [Diagrama de arquitectura](arquitectura_pr06.puml) y [explicación](P04_ARQUITECTURA.md).
- [Modelo relacional](modelo_datos_pr06.puml) y [documentación](P04_MODELO_DATOS.md).
- [Contrato YAML](openapi.yaml) y [contrato JSON](openapi.json).
- [Diagnóstico](P04_DIAGNOSTICO.md) y [trazabilidad](P04_TRAZABILIDAD.md).
