# PR06 P05 — API Spring Boot

Backend independiente para P05. Las fases 3A, 3B y 3C implementan conexión con PostgreSQL, salud, consultas REST, registro manual de entradas, cambio de estado y seguridad HTTP Basic local; no modifican ni sustituyen P02/P03.

## Versiones

- Java 11.
- Spring Boot 2.7.18.
- Maven 3.8 o posterior.
- PostgreSQL 16 según el perfil docente, con el esquema existente de PR06 previamente preparado. La integración local de Fase 3C se ejecutó sobre PostgreSQL 18.6; debe repetirse en PostgreSQL 16 para certificar exactamente ese entorno.

## Requisitos

- JDK 11 activo para Maven y para la ejecución.
- Maven disponible.
- PostgreSQL accesible con las tablas existentes.
- No es necesario ni correcto volver a ejecutar `schema.sql` o `data.sql` sobre una base ya preparada.

## Configuración

La aplicación obtiene la conexión y el puerto desde variables de entorno:

| Variable | Obligatoria | Descripción |
|---|---:|---|
| `DB_URL` | No | URL JDBC. Predeterminado: `jdbc:postgresql://localhost:5432/pr06_p02`. |
| `DB_USER` | Sí | Usuario PostgreSQL. |
| `DB_PASSWORD` | Sí | Contraseña PostgreSQL. No debe versionarse ni mostrarse en logs. |
| `API_BASIC_USER` | Sí | Usuario local para las operaciones protegidas. |
| `API_BASIC_PASSWORD` | Sí | Contraseña local. No debe versionarse ni mostrarse en logs. |
| `SERVER_PORT` | No | Puerto HTTP. Predeterminado: `8080`. |

Para capturar la contraseña sin escribirla en el historial del shell:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/pr06_p02'
export DB_USER='<usuario_local>'
read -r -s -p 'DB_PASSWORD: ' DB_PASSWORD
export DB_PASSWORD
export API_BASIC_USER='<usuario_api_local>'
read -r -s -p 'API_BASIC_PASSWORD: ' API_BASIC_PASSWORD
export API_BASIC_PASSWORD
```

## Compilación

Desde la raíz del repositorio:

```bash
JAVA_HOME=/ruta/al/jdk-11 \
PATH=/ruta/al/jdk-11/bin:$PATH \
mvn -f api-spring/pom.xml clean package
```

## Pruebas

```bash
JAVA_HOME=/ruta/al/jdk-11 \
PATH=/ruta/al/jdk-11/bin:$PATH \
mvn -f api-spring/pom.xml test
```

Las pruebas de salud sustituyen `DataSource` por un doble controlado. Las pruebas de consultas, validaciones, persistencia, seguridad y respuestas HTTP usan mocks de repositorios o servicios. También se comprueba que el repositorio de sesiones declare bloqueo pesimista de escritura. La suite automatizada no se conecta ni escribe en PostgreSQL.

## Ejecución

Con las variables de entorno configuradas:

```bash
JAVA_HOME=/ruta/al/jdk-11 \
PATH=/ruta/al/jdk-11/bin:$PATH \
mvn -f api-spring/pom.xml spring-boot:run
```

La configuración usa `spring.jpa.hibernate.ddl-auto=validate` y desactiva la inicialización SQL. La aplicación no crea ni actualiza tablas.

## Comprobación de salud

```bash
curl -i http://localhost:8080/api/health
```

Con PostgreSQL disponible:

```json
{"estado":"OK","baseDatos":"DISPONIBLE"}
```

Con PostgreSQL no disponible, la API responde HTTP 503:

```json
{"codigo":"SERVICIO_NO_DISPONIBLE","mensaje":"La base de datos no está disponible."}
```

## Endpoints

Todos los endpoints usan el prefijo `/api`:

| Método | Ruta | Resultado esperado |
|---|---|---|
| `GET` | `/health` | Estado de API y PostgreSQL. |
| `GET` | `/sesiones` | Sesiones ordenadas por fecha y hora de inicio. |
| `GET` | `/participantes` | Participantes con estado `ACTIVO`. |
| `GET` | `/registros` | Registros ordenados por fecha y hora descendente. |
| `POST` | `/registros` | Crea exclusivamente una `ENTRADA`; responde HTTP 201. |
| `PATCH` | `/sesiones/{idSesion}/estado` | Ejecuta una transición de estado permitida; responde HTTP 200. |

Los `GET` son públicos. `POST /registros` y `PATCH /sesiones/{idSesion}/estado` requieren HTTP Basic. Las consultas devuelven HTTP 200 y un arreglo JSON, incluido `[]` cuando no hay resultados.

Ejemplo de registro:

```bash
curl -i -X POST http://localhost:8080/api/registros \
  --user "$API_BASIC_USER:$API_BASIC_PASSWORD" \
  -H 'Content-Type: application/json' \
  -d '{
    "idSesion": 1,
    "idParticipante": 4,
    "tipoRegistro": "ENTRADA",
    "observacion": "Evidencia P05"
  }'
```

Respuesta HTTP 201:

```json
{
  "idRegistro": 1,
  "idSesion": 1,
  "idParticipante": 4,
  "nombreSesion": "Sesion de bienvenida",
  "nombreParticipante": "Miguel Torres",
  "tipoRegistro": "ENTRADA",
  "fechaHora": "2026-09-15T09:05:30.123456",
  "observacion": "Evidencia P05"
}
```

Ejemplo de cambio de estado:

```bash
curl -i -X PATCH http://localhost:8080/api/sesiones/1/estado \
  --user "$API_BASIC_USER:$API_BASIC_PASSWORD" \
  -H 'Content-Type: application/json' \
  -d '{"estado":"EN_CURSO"}'
```

Transiciones admitidas:

- `PROGRAMADA` → `EN_CURSO`.
- `PROGRAMADA` → `CANCELADA`.
- `EN_CURSO` → `FINALIZADA`.

Un cambio al mismo estado, retroceso u otra transición devuelve HTTP 409 con código `TRANSICION_NO_PERMITIDA`. La sesión se obtiene dentro de una transacción con `PESSIMISTIC_WRITE`, evitando que dos cambios concurrentes validen el mismo estado inicial.

## Seguridad y CSRF

- Las credenciales se mantienen únicamente en memoria y proceden de `API_BASIC_USER` y `API_BASIC_PASSWORD`.
- La contraseña se codifica con BCrypt al iniciar; no se almacena en el repositorio.
- La política de sesión es `STATELESS`: la API no conserva una sesión HTTP autenticada.
- Una autenticación ausente o incorrecta devuelve HTTP 401, JSON `NO_AUTORIZADO` y `WWW-Authenticate: Basic realm="PR06"`.
- CSRF permanece configurado para el resto de la aplicación. Se excluyen expresamente sólo `POST /registros` y `PATCH /sesiones/*/estado`, porque son operaciones JSON de esta API sin sesión ni cookies. Si se incorporan formularios web o autenticación por cookie, esta decisión debe revisarse.
- HTTP Basic debe utilizarse sólo en el entorno docente local o detrás de HTTPS; por sí solo no cifra las credenciales.

Reglas aplicadas:

- Los identificadores son obligatorios y positivos.
- La sesión y el participante deben existir, y el participante debe estar `ACTIVO`.
- El único tipo aceptado es `ENTRADA`; el estado de la sesión no condiciona el registro.
- La observación es opcional y se valida con un máximo de 200 caracteres antes de recortar espacios; vacía se guarda como `null`.
- Una combinación repetida de sesión, participante y tipo devuelve HTTP 409. La restricción `UNIQUE` de PostgreSQL conserva la protección final ante concurrencia.
- Las respuestas de error contienen solamente `codigo` y `mensaje`, sin SQL ni trazas.

Ejemplo de error controlado:

```json
{
  "codigo": "REGISTRO_DUPLICADO",
  "mensaje": "El participante ya tiene una entrada registrada en esta sesión."
}
```

## PostgreSQL aislado para pruebas manuales

No ejecute este procedimiento sobre `pr06_p02`. Elija un nombre nuevo, compruebe primero que no exista y no use `dropdb`:

```bash
export TEST_DB='pr06_p05_test'

psql -d postgres -Atc \
  "SELECT count(*) FROM pg_database WHERE datname = '$TEST_DB'"

createdb "$TEST_DB"
psql -v ON_ERROR_STOP=1 -d "$TEST_DB" -f schema.sql
psql -v ON_ERROR_STOP=1 -d "$TEST_DB" -f data.sql

export DB_URL="jdbc:postgresql://localhost:5432/$TEST_DB"
```

El resultado de la consulta previa debe ser `0`. Si devuelve `1`, elija otro nombre; no elimine ni reinicialice esa base. Configure también `DB_USER`, `DB_PASSWORD`, `API_BASIC_USER` y `API_BASIC_PASSWORD` antes de iniciar la API.

Pruebas positivas recomendadas:

1. Consultar sesiones y participantes mediante `GET`.
2. Crear una entrada autenticada y comprobar HTTP 201.
3. Recuperarla mediante `GET /api/registros` y mediante `SELECT`.
4. Cambiar una sesión de `PROGRAMADA` a `EN_CURSO` y comprobarla con `SELECT`.

Pruebas negativas recomendadas:

1. Repetir la entrada y comprobar HTTP 409.
2. Omitir o cambiar las credenciales y comprobar HTTP 401.
3. Solicitar una transición no permitida y comprobar HTTP 409 y que el estado SQL no cambió.

### Evidencia local de Fase 3C

El 26 de octubre de 2026 se creó la base nueva `pr06_p05_test_20261026` en PostgreSQL 18.6. Se cargaron 6 tablas, 2 sesiones, 4 participantes y 0 registros. La comprobación produjo:

- GET de salud, sesiones, participantes y registros: HTTP 200.
- POST sin credenciales y con credenciales incorrectas: HTTP 401.
- POST autenticado: HTTP 201 y un registro confirmado mediante `SELECT`.
- POST duplicado: HTTP 409.
- PATCH permitido: HTTP 200 y estado `EN_CURSO` confirmado mediante `SELECT`.
- PATCH no permitido: HTTP 409, conservando `EN_CURSO`.
- Dos PATCH simultáneos sobre otra sesión: uno HTTP 200 y otro HTTP 409.
- Suite automatizada: 42 pruebas aprobadas.

La base aislada se conserva como evidencia y no se eliminó ninguna base existente.

### Evidencia extremo a extremo de Fase 5A

El 28 de octubre de 2026 se repitieron las 42 pruebas con Temurin Java 11.0.32.1:
0 fallos, 0 errores y 0 omitidas. `mvn -DskipTests package` generó correctamente
el JAR después de ejecutar la suite por separado.

La API se inició configurando explícitamente `DB_URL` hacia
`pr06_p05_test_20261026`. Se confirmó `current_database()` antes de escribir. El
recorrido Angular verificó:

- GET de salud, sesiones, participantes y registros: HTTP 200.
- POST autenticado para sesión 2/participante 2: HTTP 201.
- Repetición de la entrada: HTTP 409 y sin fila adicional.
- POST sin autenticación: HTTP 401 y sin persistencia.
- PATCH de sesión 1, `EN_CURSO` → `FINALIZADA`: HTTP 200.
- Retroceso `FINALIZADA` → `EN_CURSO`: HTTP 409; SQL conservó `FINALIZADA`.

El estado final es 2 sesiones, 4 participantes y 3 registros. Las respuestas,
capturas y consultas SQL saneadas están en
`docs/P05/evidencias/README.md`. No se guardaron credenciales ni encabezados
`Authorization`.

## Limitaciones

- La seguridad es local y utiliza un único usuario en memoria; no sustituye un sistema de identidad de producción.
- La aplicación valida el esquema con `ddl-auto=validate`; nunca lo crea ni lo actualiza.
- Los scripts SQL sólo deben ejecutarse manualmente sobre una base nueva y vacía.
