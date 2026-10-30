# PR06 P05 — Frontend Angular

Frontend independiente de P05 para el sistema de control de asistencia. La Fase 4B incorpora navegación y pantallas funcionales integradas con la API Spring Boot.

## Versiones

- Node.js 18.20.8.
- npm 10.8.2.
- Angular y Angular CLI 16.2.12.
- TypeScript 5.1.6.

No se necesita una instalación global de Angular CLI. `package-lock.json` conserva las versiones resueltas.

## Instalación reproducible

Con Node 18 activo, desde `frontend-angular/`:

```bash
npm ci
```

No use `npm update` para preparar el proyecto, porque cambiaría las versiones bloqueadas.

## Conexión local

El frontend usa exclusivamente rutas relativas bajo `/api`. Durante el desarrollo, `proxy.conf.json` redirige esas solicitudes a:

```text
http://localhost:8080
```

Inicie primero `api-spring` en el puerto 8080 y después ejecute:

```bash
npm start
```

La aplicación estará disponible normalmente en `http://localhost:4200`. El proxy evita configurar CORS para el entorno local y concentra la URL del backend en un único archivo.

## Operaciones HTTP disponibles

`ApiService` implementa el contrato de `docs/P05/openapi.yaml`:

- `GET /api/health`
- `GET /api/sesiones`
- `GET /api/participantes`
- `GET /api/registros`
- `POST /api/registros`
- `PATCH /api/sesiones/{idSesion}/estado`

Todas las operaciones devuelven `Observable` tipados. Los GET nunca incluyen `Authorization`.

## Credenciales HTTP Basic

`AutenticacionService` permite establecer y limpiar credenciales en memoria. No utiliza `localStorage`, `sessionStorage`, cookies, parámetros URL ni logs. La cabecera Basic sólo se genera para POST/PATCH dirigidos a la ruta relativa local `/api`.

La cabecera de la aplicación permite capturar y borrar las credenciales. Guardarlas no significa que estén autenticadas: la interfaz sólo informa que el backend las aceptó después de una operación POST o PATCH exitosa. No se realizan escrituras de prueba para validarlas.

HTTP Basic sobre HTTP sin TLS se admite únicamente en el laboratorio local. En otro entorno debe usarse HTTPS.

## Pantallas y rutas

- `/`: salud de la API y PostgreSQL.
- `/sesiones`: consulta de sesiones y cambios PROGRAMADA → EN_CURSO/CANCELADA o EN_CURSO → FINALIZADA.
- `/registrar`: formulario de ENTRADA con sesiones, participantes activos y observación de hasta 200 caracteres.
- `/registros`: consulta de los registros persistidos.

Las consultas incluyen estados de carga, lista vacía y error. Las operaciones protegidas muestran respuestas diferenciadas para validación, autenticación, referencias inexistentes, duplicados o transiciones rechazadas.

## Pruebas

```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

Las pruebas usan `HttpClientTestingModule`; no requieren PostgreSQL, el backend real ni servicios externos. Comprueban navegación, rutas y métodos HTTP, carga, listas vacías, errores, formulario, doble envío, autenticación en memoria, registro y cambios de estado.

La configuración incluida usa `ChromeHeadless`. Si el equipo no dispone de Chrome, puede emplearse otro navegador ya instalado mediante una configuración local de Karma; no es necesario cambiar las dependencias de la aplicación.

## Build de producción

```bash
npm run build -- --configuration production
```

El resultado se genera en `dist/frontend-angular/`.

## Verificación

La Fase 5A se verificó el 28 de octubre de 2026 con Node 18.20.8, npm
10.8.2, Angular/CLI 16.2.12 y TypeScript 5.1.6. Las 29 pruebas automatizadas
fueron aprobadas con Chrome for Testing 156 en modo headless y el build de
producción finalizó correctamente.

Se inició temporalmente el backend con Java 11 y `DB_URL` dirigido
explícitamente a la base aislada `pr06_p05_test_20261026`. Desde Chrome real y
a través del proxy Angular se comprobaron las cuatro rutas, GET de salud,
sesiones, participantes y registros, POST autenticado HTTP 201, duplicado HTTP
409, POST sin credenciales HTTP 401, PATCH permitido HTTP 200 y retroceso HTTP
409.

SQL confirmó una sola fila nueva para sesión 2/participante 2, ninguna para el
intento sin autenticación y la sesión 1 en `FINALIZADA` después del PATCH
rechazado. El índice de respuestas, capturas y consultas saneadas se encuentra
en `docs/P05/evidencias/README.md`.

## Revisión de dependencias

`npm audit` informa 49 incidencias para las versiones fijadas por el perfil docente: 6 bajas, 18 moderadas, 23 altas y 2 críticas. Al excluir dependencias de desarrollo quedan 8 incidencias en paquetes Angular de ejecución: 4 moderadas y 4 altas. Las 41 restantes pertenecen a herramientas de compilación, CLI y desarrollo, incluidas las 2 críticas.

No se ejecutó `npm audit fix`, ya que actualizar automáticamente estas dependencias puede romper la compatibilidad exigida con Angular 16.2.12. Debe revisarse una migración controlada cuando el perfil docente autorice cambiar de versión.

## Limitaciones

- Las reglas de negocio permanecen en Spring Boot; Angular no intenta reproducirlas.
- La autenticación es local y en memoria: al recargar la página se eliminan las credenciales.
- No existe una operación de autenticación independiente; la aceptación se conoce únicamente después de un POST o PATCH real.
- No se implementan SALIDA, edición ni eliminación de registros.
