# P03 — Diagramas y guía de evidencias del documento

Fuente revisada: `P03_PR06_Aplicacion_Web_2_JSF_PrimeFaces_PostgreSQL.docx`.
Esta guía identifica las evidencias de la plantilla; no amplía el sistema ni
modifica la base de datos. Los comandos siguientes son para ejecutarlos tú.
Los números 01–16 sustituyen, en el mismo orden, los marcadores «Figura 0.0».
El índice de la sección 14 contiene 15 figuras; la sección 15 agrega la figura 16.

## 1. Índice completo: qué capturar y dónde

| Figura | Sección DOCX / evidencia | Tipo | Contenido concreto |
| --- | --- | --- | --- |
| 01 | 5 / E05 | Diagrama | `diagramas/01-modelo-relacional.puml`: seis tablas, PK, FK, unicidad y cardinalidades. Insertar el PNG generado. |
| 02 | 6.1 / E11 | Diagrama | `diagramas/02-ciclo-vida-jsf.puml`: seis fases y desvío a Render Response cuando falla validación. |
| 03 | 6.2 / E01 | IDE | `registro.xhtml`, no solo `index.xhtml`: `h:form`, `p:messages`, selectores, enlaces `#{registroBean...}`, botón y process/update. Dos recortes 03a/03b si no cabe legible. |
| 04 | 6.3 / E02 | IDE | `RegistroBean.java`: 04a anotaciones ManagedBean/ViewScoped y propiedades; 04b método registrar(), consulta de duplicado, llamada insertarRegistro() y mensaje de éxito. |
| 05 | 7 / E03, E07 | Navegador | `registro.xhtml` antes del envío: sesión, participante, ENTRADA, observación, botón y navegación. El área p:messages está vacía inicialmente; se evidencia en el código y después del envío. |
| 06 | 7 / E04 | Navegador + SQL | Formulario con sesión/participante vacíos y mensajes obligatorios. Acompañar con conteo SQL antes/después sin cambio. Prueba PN03-01. |
| 07 | 7 / E04 | Navegador + SQL | Misma pareja y ENTRADA ya guardados: mensaje de duplicado. Consulta de esa pareja continúa con una sola fila. PN03-02. |
| 08 | 8 / E08 | Navegador + DevTools | 08a antes, 08b después del registro; 08c Network con XHR, process/render y respuesta partial-response. Sin una nueva petición Document. |
| 09 | 9 / E05 | Terminal | Ejecución correcta de schema.sql/data.sql y listado de las seis tablas. Usar instalación nueva o la base separada de demostración indicada abajo. |
| 10 | 9 / E06 | Terminal SQL | Filas de registro y conteo ANTES del envío válido. La pareja elegida debe tener cero ENTRADAS. |
| 11 | 9 / E03, E06 | Navegador | Éxito del registro válido y fila nueva, con observación identificable. PP03-02. |
| 12 | 9 / E06 | Terminal SQL | Mismo registro posterior, con ID, sesión, código del participante, tipo y observación. Añadir 12b de registros.xhtml tras recargar para PR03-01. |
| 13 | 10 / E09 | Terminal | Maven clean/validate/compile/package, BUILD SUCCESS y nombre del WAR. |
| 14 | 10 / E09 | Terminal o Servers | WAR en target y despliegue en Tomcat 9 con contexto /pr06_p02. El archivo por sí solo no acredita despliegue. |
| 15 | 10 / E07 | Navegador | URL localhost visible, TabMenu y tabla de sesiones cargada; complemento con selector de participantes para PP03-01. |
| 16 | 15 / E10 | Repositorio + IDE | Estructura, README, SQL, módulo, diagramas y commit de entrega que incluya realmente los archivos mostrados. |

## 2. Preparación

Trabaja desde la raíz del repositorio. No abras `.env` ni `ConexionDB.java` en
las capturas. Las contraseñas se introducen en el prompt de psql (`-W`), no en
comandos ni en URLs. Puedes usar `PS1='$ '` en la terminal de evidencia para
que el prompt no muestre tu ruta personal; no cambia el directorio ni el sistema.

Configura variables NO sensibles para los comandos de PostgreSQL:

```bash
export PGHOST=localhost
export PGPORT=5432
export PGUSER=postgres
export PGDATABASE=pr06_p02
```

Deben coincidir con tu configuración local existente. Estas variables son para
las herramientas PostgreSQL: no cambian `ConexionDB.java` ni cargan `.env`.

## 3. Figuras 01 y 02 — Generar los diagramas

En tu instalación se encontró PlantUML dentro de la extensión de VS Code.
Desde la raíz, usa Java y Graphviz ya disponibles:

```bash
P03_PLANTUML_JAR="$HOME/.vscode/extensions/jebbs.plantuml-2.18.1/plantuml.jar"
java -jar "$P03_PLANTUML_JAR" -charset UTF-8 -checkonly 'docs/p03/diagramas/*.puml'
java -jar "$P03_PLANTUML_JAR" -charset UTF-8 -tpng 'docs/p03/diagramas/*.puml'
java -jar "$P03_PLANTUML_JAR" -charset UTF-8 -tsvg 'docs/p03/diagramas/*.puml'
```

Si la extensión cambia de versión, ajusta únicamente la ruta del JAR.
También puedes abrir cada `.puml` en VS Code y ejecutar **PlantUML: Preview
Current Diagram** o **PlantUML: Export Current Diagram** desde la paleta.
Inserta los PNG en Word; conserva los SVG para exportar con mayor resolución.
Para el modelo relacional conviene una página horizontal.

El diagrama relacional no inventa restricciones: `evento.id_participante` permite
NULL, igual que `incidencia.justificacion`. El documento menciona justificación
de correcciones, pero eso no es NOT NULL en el SQL ni una funcionalidad de P03.

## 4. Figura 09 — Scripts SQL sin dañar la base que usas

**Si pr06_p02 ya tiene tablas, no repitas schema.sql/data.sql allí.** Para obtener
una evidencia real de instalación limpia, usa una base adicional vacía, solo
para esta demostración (no requiere cambiar la conexión de la aplicación):

```bash
createdb -h localhost -p 5432 -U postgres -W pr06_p03_evidencia_scripts
psql -X -h localhost -p 5432 -U postgres -W -d pr06_p03_evidencia_scripts \
  -v ON_ERROR_STOP=1 --single-transaction -f schema.sql -f data.sql
psql -X -h localhost -p 5432 -U postgres -W -d pr06_p03_evidencia_scripts \
  -c '\dt public.*' -c 'SELECT * FROM sesion;' \
  -c 'SELECT codigo_ficticio, nombre, apellido, estado FROM participante;'
```

Captura CREATE TABLE, INSERT y las seis tablas. Si ese nombre de base ya existe,
elige otro nuevo y úsalo en los tres comandos; no borres una base para repetir.
Pie sugerido: «Ejecución de los scripts del proyecto en una base vacía de
verificación; las pruebas de interfaz se realizan en pr06_p02».
Esta figura demuestra reproducibilidad de los scripts, no una inserción de la UI.
Si estás preparando pr06_p02 por primera vez, puedes usar ese nombre en lugar
de la base adicional. No se ejecutó ninguno de estos comandos al crear esta guía.

## 5. Figuras 13 y 14 — Compilar y desplegar

Verifica las versiones que realmente empleas:

```bash
java -version
mvn -version
psql --version
psql -X -W -c 'SHOW server_version;'
/opt/tomcat9/bin/version.sh
```

`psql --version` describe el cliente; `SHOW server_version` describe el servidor.
Para compilar con JDK 11 instalado sin cambiar la configuración global:

```bash
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 mvn -B -f pr06_p02/pom.xml clean validate compile package
ls -lh pr06_p02/target/pr06_p02.war
jar tf pr06_p02/target/pr06_p02.war | rg '(^[^/]+\.xhtml$|myfaces|primefaces|postgresql)'
```

Captura el final de Maven con `BUILD SUCCESS`, nombre/ruta del WAR y su tamaño.
Con Tomcat 9 detenido, despliega en tu instalación:

```bash
cp pr06_p02/target/pr06_p02.war /opt/tomcat9/webapps/
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
JRE_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
/opt/tomcat9/bin/catalina.sh run
```

Mantén esa terminal abierta. Si el servidor ya está gestionado por VS Code,
usa **Run on Server** para redeplegar el WAR en ese Tomcat 9; no arranques otra
instancia en 8080. Si usas otra instalación, sustituye `/opt/tomcat9` por su ruta.
Captura el despliegue exitoso de `pr06_p02.war` y complementa con:

```bash
curl -i http://localhost:8080/pr06_p02/health
```

Debe devolver HTTP 200 y `OK - Conexion a PostgreSQL establecida`.
El WAR no cambió de nombre: P03 sigue dentro de `pr06_p02`.

## 6. Figuras 03 y 04 — Código en el IDE

```bash
code -g pr06_p02/src/main/webapp/registro.xhtml:22
code -g pr06_p02/src/main/java/com/pr06/asistencia/bean/RegistroBean.java:20
```

- **03a:** líneas aproximadas 22–45, h:form, messages, selector y propiedades.
- **03b:** líneas 58–75, validación de longitud, botón con acción/process/update y tabla.
- **04a:** líneas 20–34, anotaciones y propiedades.
- **04b:** líneas 116–149, DAO, duplicado, inserción y mensaje de éxito.

Usa dos imágenes cuando sea necesario, sin reducir el código hasta hacerlo
ilegible. Los números corresponden a la versión revisada y pueden desplazarse.
No abras el archivo de conexión para probar el acceso a datos: basta el DAO y
el código del bean sin credenciales.

## 7. Orden de ejecución para figuras 05–08, 10–12 y 15

El orden de capturar puede diferir del orden de insertar las figuras en Word.
Primero toma el formulario vacío y la prueba negativa; después registra una vez,
comprueba persistencia y finalmente repite para demostrar el duplicado.

### 7.1 Carga y formulario (05 y 15 / PP03-01)

Abre estas URLs en el navegador:

- `http://localhost:8080/pr06_p02/index.xhtml`: inicio y TabMenu.
- `http://localhost:8080/pr06_p02/sesiones.xhtml`: sesiones desde PostgreSQL.
- `http://localhost:8080/pr06_p02/registro.xhtml`: formulario y participantes activos.
- `http://localhost:8080/pr06_p02/registros.xhtml`: consulta persistida.

Para la figura 05, captura el formulario antes de enviar. Puedes tomar otra imagen
con el selector de participantes abierto. Para la 15, incluye barra de dirección,
pestaña Sesiones activa y filas legibles. No uses una URL `.jsp` como evidencia JSF.

### 7.2 Abrir psql y elegir una pareja disponible

```bash
psql -X -W
```

Dentro de psql:

```sql
\pset pager off
SELECT s.id_sesion, s.nombre AS sesion, p.id_participante,
       p.codigo_ficticio, p.nombre, p.apellido
FROM sesion s CROSS JOIN participante p
WHERE p.estado = 'ACTIVO'
  AND NOT EXISTS (
    SELECT 1 FROM registro r
    WHERE r.id_sesion = s.id_sesion
      AND r.id_participante = p.id_participante
      AND r.tipo_registro = 'ENTRADA'
  )
ORDER BY s.id_sesion, p.id_participante;
```

Elige una fila de ese resultado. Define sus IDs en la misma sesión de psql;
cuando aparezca cada prompt, introduce el número elegido:

```sql
\prompt 'ID de sesion elegido: ' sesion_prueba
\prompt 'ID de participante elegido: ' participante_prueba
```

No asumas que una pareja está libre: ya existen registros de verificaciones
anteriores. Si no hay filas disponibles, detente y prepara una base de pruebas
vacía de manera deliberada; no borres los datos de trabajo automáticamente.

### 7.3 Campo obligatorio (06 / PN03-01)

En psql, ANTES:

```sql
SELECT count(*) AS total_antes_negativa FROM registro;
```

Abre el formulario sin elegir sesión ni participante y pulsa **Registrar entrada**.
Captura los mensajes «Debe seleccionar una sesión» y «Debe seleccionar un
participante». En psql, DESPUÉS:

```sql
SELECT count(*) AS total_despues_negativa FROM registro;
```

Ambos conteos deben coincidir. Haz la prueba sin otros usuarios registrando a la
vez. La captura del mensaje por sí sola no acredita ausencia de inserción.

### 7.4 Estado inicial (10)

Antes del registro válido, ejecuta y captura:

```sql
SELECT id_registro, id_sesion, id_participante, tipo_registro, fecha_hora, observacion
FROM registro ORDER BY id_registro;
SELECT count(*) AS total_antes FROM registro;
SELECT count(*) AS entradas_de_la_pareja_antes
FROM registro
WHERE id_sesion = :sesion_prueba AND id_participante = :participante_prueba
  AND tipo_registro = 'ENTRADA';
```

La última consulta debe mostrar 0. No cierres esta sesión psql: mantiene los IDs.

### 7.5 Registro válido y AJAX (08 y 11 / PP03-02)

1. En el formulario selecciona los nombres correspondientes a esos mismos IDs.
2. Mantén ENTRADA y escribe una observación identificable, por ejemplo
   `Evidencia P03 2026-09-24 A`. Guarda esa observación para comparar con SQL.
3. Toma **08a** antes de enviar.
4. Abre F12 → **Network / Red**. Limpia la lista y selecciona **All / Todo**
   para poder comprobar que no hay una petición nueva de tipo Document.
5. Pulsa **Registrar entrada** una sola vez.
6. Toma **08b/11** con el mensaje de éxito y la nueva fila. Una misma captura
   puede servir para ambos apartados si la referencias claramente.
7. Toma **08c** seleccionando la petición XHR POST a `registro.xhtml`:
   - Payload: `javax.faces.partial.ajax=true`.
   - `javax.faces.partial.execute`: `registroForm:registrar registroForm:campos`.
   - `javax.faces.partial.render`: `registroForm:mensajes registroForm:registros`.
   - Response: `<partial-response>` con `<update id="registroForm:mensajes">`
     y `<update id="registroForm:registros">` (también se actualiza ViewState).

Recorta encabezados de cookies/identificadores de sesión si aparecen. No basta
con que la URL no cambie: la prueba fuerte es la petición y respuesta parciales.
La navegación del TabMenu sí solicita otro XHTML mediante GET; no la presentes
como navegación AJAX. **Actualizar registros** y **Actualizar sesiones** también
son acciones parciales, con `process="@this"`, sin validar el formulario de entrada.

### 7.6 SQL posterior y recarga (12 / PR03-01)

En la misma sesión psql:

```sql
SELECT r.id_registro, r.id_sesion, s.nombre AS sesion,
       r.id_participante, p.codigo_ficticio,
       p.nombre || ' ' || p.apellido AS participante,
       r.tipo_registro, r.fecha_hora, r.observacion
FROM registro r
JOIN sesion s ON s.id_sesion = r.id_sesion
JOIN participante p ON p.id_participante = r.id_participante
WHERE r.id_sesion = :sesion_prueba AND r.id_participante = :participante_prueba
  AND r.tipo_registro = 'ENTRADA';
SELECT count(*) AS total_despues FROM registro;
```

La primera consulta muestra exactamente una fila, con la observación de 08/11;
el total debe subir en uno. Captura **12a**. Abre `registros.xhtml`, recarga y toma
**12b** mostrando el mismo ID y observación. Así se demuestra consulta persistida,
no solo estado del bean.

### 7.7 Duplicado (07 / PN03-02)

Vuelve a `registro.xhtml`, selecciona la misma pareja, ENTRADA y pulsa registrar.
Captura el mensaje de rechazo. Cambiar la observación no evita el duplicado.
Después ejecuta:

```sql
SELECT count(*) AS entradas_de_la_pareja_despues_del_duplicado
FROM registro
WHERE id_sesion = :sesion_prueba AND id_participante = :participante_prueba
  AND tipo_registro = 'ENTRADA';
SELECT count(*) AS total_despues_del_duplicado FROM registro;
SELECT id_sesion, id_participante, tipo_registro, count(*)
FROM registro
GROUP BY id_sesion, id_participante, tipo_registro
HAVING count(*) > 1;
```

Resultados esperados: 1 para la pareja, mismo total que después del registro
válido y cero filas en la consulta de duplicados. Finaliza psql con `\q`.

## 8. Figura 16 — README y repositorio

```bash
git log -1 --format='%h %s'
git status --short
git tag --list P03
```

Abre `https://github.com/JeshuaBenitez/PR06_DSW-E06` y captura la estructura y el
README de la revisión realmente entregada. En el IDE muestra `docs/p03`,
`schema.sql`, `data.sql`, `pr06_p02/pom.xml` y `src/main/webapp`.

El tag P03 apunta al commit inicial `54b7f40`. Las mejoras de TabMenu,
responsive/AJAX y estos diagramas se incorporan en commits posteriores de master.
**Ese tag no incluye estas mejoras.** Para la evidencia final, utiliza el commit
de master que contenga los archivos mostrados y compruébalo con `git log -1`.
El tag original se conserva. El cambio local de `ConexionDB.java` permanece
excluido de la entrega.

Datos para completar la plantilla:

- Repositorio: `https://github.com/JeshuaBenitez/PR06_DSW-E06`.
- Commit/tag: anotar la revisión que incluya la entrega final, comprobándola.
- WAR: `pr06_p02.war`.
- Contexto: `/pr06_p02`.

## 9. Ajustes del documento antes de entregar

- Secciones 4 y 7: el flujo implementado permite solo ENTRADA. No presentar SALIDA,
  incidencias ni correcciones como funciones disponibles.
- Sección 10: el POM usa **MyFaces 2.3.10**, no Mojarra 2.3.9; PrimeFaces es 12.0.0.
  Declara las versiones reales de Maven y PostgreSQL a partir de los comandos.
  No afirmes que una diferencia está autorizada si no cuentas con esa autorización.
- El esquema admite NULL en justificación y en el participante de evento; las
  cardinalidades del diagrama reflejan los scripts, no requisitos futuros.
- Mantén las pruebas del DOCX en PENDIENTE hasta ejecutar este recorrido y guardar
  sus capturas. Pasa a VERIFICADO solo con evidencia; ante fallo usa NO_VERIFICADO.
  La generación de diagramas no prueba el despliegue ni la persistencia.
- La sección 18 dice que la IA se usó únicamente para redacción. Eso no describe
  esta colaboración, que también incluyó código, pruebas y diagramas. Ajusta la
  declaración para reflejar el uso real y la revisión que haga el equipo.
- Verifica los nombres del académico, del equipo (DSQ/DSW) e integrantes: la
  plantilla contiene variantes y campos por completar.

## Fuentes técnicas

El modelo se deriva de `schema.sql`; el flujo aplicado, de `RegistroBean.java`
y `registro.xhtml`. Referencias para las fases y el procesamiento parcial:

- [Ciclo de vida JSF — Java EE Tutorial](https://docs.oracle.com/javaee/7/tutorial/jsf-intro006.htm).
- [Ciclo de solicitudes AJAX — Java EE Tutorial](https://javaee.github.io/tutorial/jsf-ajax008.html).
