# P04 — Modelo de datos real

Fuente principal: [docs/P04/modelo_datos_pr06.puml](modelo_datos_pr06.puml).

El diagrama reproduce [schema.sql](../../schema.sql), contrastado con [data.sql](../../data.sql), los modelos de `pr06_p02/src/main/java/com/pr06/asistencia/model/` y los DAO JDBC correspondientes. No añade columnas ni relaciones.

## Cobertura

PRESENTE indica que el elemento existe; PARCIAL, que solo existe una parte de su soporte; PENDIENTE, que no existe en la capa indicada. El estado global combina SQL y soporte Java actual, sin implicar un CRUD completo.

| Entidad | Tabla SQL | Modelo / DAO JDBC | Entidad JPA | Estado global |
|---|---|---|---|---|
| sesion | PRESENTE | PRESENTE | PENDIENTE | PRESENTE |
| participante | PRESENTE | PRESENTE | PENDIENTE | PRESENTE |
| registro | PRESENTE | PRESENTE | PENDIENTE | PRESENTE |
| incidencia | PRESENTE | PENDIENTE | PENDIENTE | PARCIAL |
| dispositivo | PRESENTE | PENDIENTE | PENDIENTE | PARCIAL |
| evento | PRESENTE | PENDIENTE | PENDIENTE | PARCIAL |

No falta ninguna de las seis tablas exigidas. No se crean clases para completar silenciosamente los elementos pendientes.

## Relaciones

| Padre | Hijo / FK | Cardinalidad |
|---|---|---|
| sesion | registro.id_sesion | Una sesión tiene 0..N registros; cada registro requiere una sesión. |
| participante | registro.id_participante | Un participante tiene 0..N registros; cada registro requiere un participante. |
| registro | incidencia.id_registro | Un registro tiene 0..N incidencias; cada incidencia requiere un registro. |
| dispositivo | evento.id_dispositivo | Un dispositivo tiene 0..N eventos; cada evento requiere un dispositivo. |
| sesion | evento.id_sesion | Una sesión tiene 0..N eventos; cada evento requiere una sesión. |
| participante | evento.id_participante | Un participante tiene 0..N eventos; cada evento admite 0..1 participante. |

## Restricciones y correspondencia

- Todas las PK son SERIAL: enteros autogenerados, no BIGINT.
- `participante.codigo_ficticio` y `dispositivo.identificador_ficticio` son UNIQUE y NOT NULL.
- `registro` tiene UNIQUE (`id_sesion`, `id_participante`, `tipo_registro`). La protección también se aplica a inserciones concurrentes.
- `fecha_hora` en registro, incidencia y evento tiene DEFAULT CURRENT_TIMESTAMP, pero admite NULL explícito.
- Las FK no declaran CASCADE ni otra acción especial. No se inventan relaciones directas entre incidencia y participante o entre registro y dispositivo.
- No existen CHECK para los estados, ENTRADA o el orden de las horas. ENTRADA y participante activo son reglas actuales de la aplicación.
- `Registro.nombreSesion` y `Registro.nombreParticipante` provienen de JOIN y composición de nombres en el DAO; no son columnas físicas de registro.
- Todos los atributos y tamaños VARCHAR están incluidos en el `.puml`. El asterisco identifica campos obligatorios; los restantes admiten NULL.

`data.sql` contiene dos sesiones, cuatro participantes y un dispositivo de prueba. No define nuevas relaciones ni sustituye las restricciones del esquema.

## Separación del contrato REST

Los esquemas de [openapi.yaml](openapi.yaml) y [openapi.json](openapi.json) son representaciones JSON propuestas, con nombres camelCase. No constituyen nuevas columnas SQL. `fechaHora` se propone como fecha y hora local sin zona, respetando TIMESTAMP sin zona del esquema; la política de zona horaria queda pendiente de implementación.

## Entities y DTOs propuestos

El diagrama incluye una nota de correspondencia entre las seis tablas reales y `SesionEntity`, `ParticipanteEntity`, `RegistroEntity`, `IncidenciaEntity`, `DispositivoEntity` y `EventoEntity`, todas pendientes de implementación. Los atributos, restricciones y relaciones SQL se conservan.

Los DTOs (`RegistroEntradaDTO`, `SesionDTO`, `ParticipanteDTO`, `RegistroDTO`, `ErrorDTO`) representan el contrato de intercambio, no entidades persistentes. Sus propiedades corresponden a los esquemas OpenAPI existentes; no se crean tablas para ellos. La conversión DTO/entity se ubica en Service. Angular consume esas representaciones JSON mediante su servicio HTTP.

La composición de páginas y microcomponentes se documenta en [arquitectura](P04_ARQUITECTURA.md); no modifica el modelo relacional.
