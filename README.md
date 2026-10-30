# PR06 — Control de asistencia con eventos

Proyecto académico de Desarrollo de Sistemas Web para consultar sesiones y participantes, registrar asistencia manual de tipo `ENTRADA`, evitar duplicados y consultar los registros persistidos. P05 reutiliza las reglas funcionales de P02/P03, sin sustituir el legado.

> **Estado de publicación de `pr06-p05`:** el árbol actual de esta rama contiene artefactos generados de Angular, pero no contiene los fuentes, Dockerfiles ni documentación P05. Esos archivos existen en el commit local de `master` `30eaa7c0`. **No publique aún esta rama como una entrega P05 funcional.** Integre primero ese contenido y retire del índice los artefactos generados indicados en [Preparación para GitHub](#preparación-para-github). Las secciones P05 describen la implementación que debe acompañar la rama tras dicha integración.

## Evolución del proyecto

| Incremento | Tecnología | Alcance |
| --- | --- | --- |
| P02 | JSP, Servlets, JDBC y PostgreSQL | Implementación Web 1.0 original. Permanece en `pr06_p02/`. |
| P03 | JSF 2.3, PrimeFaces y PostgreSQL | Evolución de interfaz y evidencia histórica. |
| P05 | Angular 16, Spring Boot y PostgreSQL | API REST, SPA, HTTP Basic local, pruebas y Docker Compose. |

`pr06_p02/` conserva P02/P03; `docs/P04/` conserva los contratos y diagramas históricos.

## Arquitectura P05

```text
Navegador → Angular 16.2.12 + Nginx → /api → Spring Boot 2.7.18 / Java 11 → PostgreSQL 16
```

Nginx entrega la SPA y reenvía `/api` al backend por la red interna Docker. Spring Boot utiliza JPA/JDBC con `ddl-auto=validate`: valida el esquema, pero no crea ni actualiza tablas.

## Tecnologías

| Componente | Versión / uso |
| --- | --- |
| Java / Spring Boot | 11 / 2.7.18 |
| Angular / Angular CLI | 16.2.12 |
| Node.js / npm | 18.20.8 / 10.8.2 en la verificación documentada |
| TypeScript / RxJS | 5.1.x / 7.8.x |
| PostgreSQL | 16 para Docker |
| OpenAPI | 3.0.3 |

La evidencia local histórica se ejecutó en PostgreSQL 18.6; debe repetirse en PostgreSQL 16 si se exige certificación exacta del perfil docente.

## Funcionalidades P05

- Salud, sesiones, participantes activos y registros mediante consultas públicas.
- Registro manual exclusivamente de `ENTRADA`.
- Validación de IDs positivos, referencias existentes, participante `ACTIVO` y observación opcional de hasta 200 caracteres antes de recortar espacios.
- Rechazo de duplicados; PostgreSQL conserva una restricción `UNIQUE` como protección final de concurrencia.
- Cambio protegido de estado: `PROGRAMADA → EN_CURSO`, `PROGRAMADA → CANCELADA` y `EN_CURSO → FINALIZADA`.
- HTTP Basic local y sin sesión para `POST /api/registros` y `PATCH /api/sesiones/{idSesion}/estado`; los `GET` son públicos.
- Errores JSON con `codigo` y `mensaje`, sin SQL, trazas ni secretos.

No se implementan `SALIDA`, edición o eliminación de registros, JWT, OAuth ni usuarios persistentes.

## Estructura esperada tras integrar P05

```text
PR06/
├── pr06_p02/                 # P02/P03
├── api-spring/               # Backend Spring Boot P05
├── frontend-angular/          # SPA Angular P05
├── docs/P04/                 # Histórico
├── docs/P05/                 # OpenAPI, decisiones y evidencias E2E
├── schema.sql y data.sql      # Esquema y datos ficticios reutilizados
├── compose.yaml               # Frontend + backend + PostgreSQL Docker
└── .env.example              # Plantilla sin secretos
```

## Requisitos y variables de entorno

Para Docker se necesita Docker Engine con Docker Compose y los puertos `4200`, `8080` y `5433` disponibles (o valores alternativos). Para ejecución individual: JDK 11, Maven, Node.js 18 y PostgreSQL accesible.

Tras integrar P05, cree la configuración local:

```bash
cp .env.example .env
```

Complete sólo en `.env` las contraseñas de PostgreSQL y HTTP Basic. Ese archivo está ignorado por Git. Nunca publique contraseñas, encabezados `Authorization` ni URLs con credenciales.

Variables Docker: `PR06_DOCKER_DB_NAME`, `PR06_DOCKER_DB_USER`, `PR06_DOCKER_DB_PASSWORD`, `PR06_DOCKER_BASIC_USER`, `PR06_DOCKER_BASIC_PASSWORD`, `PR06_FRONTEND_PORT`, `PR06_BACKEND_PORT` y `PR06_DB_PORT`.

## Ejecución integrada con Docker Compose

Con `.env` completado y el contenido P05 integrado:

```bash
docker compose up -d --build
```

| Servicio | URL |
| --- | --- |
| Angular | http://localhost:4200 |
| API directa | http://localhost:8080/api/health |
| Salud por proxy Angular | http://localhost:4200/api/health |
| PostgreSQL local | localhost:5433 |

Para detener sin borrar datos:

```bash
docker compose stop
# o
docker compose down
```

El volumen `pr06_p05_postgres_data` conserva la base Docker. `docker compose down -v` elimina ese volumen y sus datos; úselo sólo para una reinicialización deliberada. `schema.sql` y `data.sql` se ejecutan únicamente en la primera inicialización de un volumen vacío.

```bash
docker compose logs -f
docker compose logs -f backend
docker compose logs -f db
```

## HTTP Basic, sesiones y registros

La SPA mantiene las credenciales únicamente en memoria y las usa sólo contra el origen local configurado. HTTP Basic sin TLS es adecuado sólo para laboratorio local; en producción requiere HTTPS y una solución de identidad apropiada.

Flujo: consultar sesiones y participantes activos, introducir credenciales locales, crear una `ENTRADA`, consultar registros y comprobar que una repetición devuelve `409`. El estado de sesión no condiciona el registro de `ENTRADA`, por compatibilidad con P02/P03.

## API REST

El prefijo es `/api`. El contrato fuente, al integrar P05, es `docs/P05/openapi.yaml`.

| Método | Ruta | Acceso | Respuestas principales |
| --- | --- | --- | --- |
| `GET` | `/api/health` | Público | `200`, `503`, `500` |
| `GET` | `/api/sesiones` | Público | `200`, `500` |
| `GET` | `/api/participantes` | Público | `200`, `500` |
| `GET` | `/api/registros` | Público | `200`, `500` |
| `POST` | `/api/registros` | HTTP Basic | `201`, `400`, `401`, `404`, `409`, `500` |
| `PATCH` | `/api/sesiones/{idSesion}/estado` | HTTP Basic | `200`, `400`, `401`, `404`, `409`, `500` |

La salud devuelve `503` si PostgreSQL no está disponible. Las respuestas negativas exponen sólo `codigo` y `mensaje`.

## Persistencia PostgreSQL

Docker usa una base nueva (`pr06_p05_docker` por defecto) dentro de un volumen exclusivo. No se conecta a `pr06_p02` ni a bases aisladas históricas. Los montajes de `schema.sql` y `data.sql` son de sólo lectura y usan `:Z` en SELinux para que PostgreSQL pueda leerlos sin ampliar permisos Unix.

## Compilación individual

Backend, con JDK 11:

```bash
JAVA_HOME=/ruta/al/jdk-11 PATH=/ruta/al/jdk-11/bin:$PATH \
  mvn -f api-spring/pom.xml clean package
```

Frontend, con Node 18:

```bash
cd frontend-angular
npm ci
npm run build
```

La ejecución local del backend requiere `DB_URL`, `DB_USER`, `DB_PASSWORD`, `API_BASIC_USER` y `API_BASIC_PASSWORD`. Use una base nueva y aislada para pruebas de escritura; no reinicialice `pr06_p02`.

## Pruebas y evidencias

El commit local `master` `30eaa7c0` documenta, el 28 de octubre de 2026, 42 pruebas backend aprobadas, 29 pruebas Angular aprobadas, builds Maven/Angular correctos y un recorrido E2E Angular → API → PostgreSQL aislado que verificó salud, consultas, `201`, `401`, `409` y cambio de estado.

La evidencia usó `pr06_p05_test_20261026`, distinta de `pr06_p02`. Tras integrar P05, las capturas, respuestas saneadas y consultas SQL estarán en `docs/P05/evidencias/`. No debe presentarse esa evidencia como una ejecución nueva de la rama actual.

## Referencias técnicas

- `docs/P05/openapi.yaml` y `docs/P05/openapi.json`: contrato REST.
- `docs/P05/P05_DECISIONES_TECNICAS.md`: decisiones técnicas.
- `api-spring/README.md` y `frontend-angular/README.md`: guías por módulo.
- `docs/P05/evidencias/README.md`: índice de pruebas extremo a extremo.
- `docs/P04/`: evidencia histórica.

## Preparación para GitHub

`.gitignore` excluye dependencias, cachés, productos de compilación, secretos, temporales y logs; `.env.example` permanece como plantilla sin credenciales reales.

El commit `d66cc84a` versiona aproximadamente 22 790 archivos generados de `frontend-angular/node_modules/` y `frontend-angular/dist/` (≈241 MiB de blobs sin comprimir). Su blob mayor es `esbuild.wasm` (≈11 MiB). No hay blobs de 50 MiB o más en las referencias locales, pero esos archivos deben salir del índice y no volver a añadirse.

Después de integrar los fuentes P05 de `master`, la limpieza segura es:

```bash
git rm -r --cached frontend-angular/node_modules frontend-angular/dist
git add .gitignore .env.example README.md api-spring frontend-angular docs/P05 compose.yaml
git status
git commit -m "chore(p05): preparar fuentes y excluir artefactos generados"
git push origin pr06-p05
```

Esto evita nuevos artefactos, pero no reduce objetos ya presentes en el historial. Si GitHub exige eliminar esos objetos históricos, se requiere autorización específica para reescribir `pr06-p05` con `git filter-repo` o crear una rama limpia; no lo haga automáticamente.
