# PR06 - Control de asistencia con eventos

## 1. Descripción

PR06 es un proyecto académico para controlar la asistencia a sesiones mediante eventos. La actividad P02 implementa el primer incremento Web 1.0 y permite consultar sesiones, seleccionar participantes ficticios, registrar manualmente una entrada, rechazar entradas duplicadas y consultar los registros persistidos en PostgreSQL.

Datos del documento de evidencia `Capturas de evidencia/PR06_P02.pdf`:

- EE: Desarrollo de sistemas web.
- NRC: 19559.
- Académico: Rodriguez Vazquez Gabriel.
- Autor: DSW-E06.
- Facultad de Negocios y Tecnologías, Región Orizaba - Córdoba, Universidad Veracruzana.

Requerimientos funcionales cubiertos en este incremento:

| Requerimiento | Implementación |
| --- | --- |
| RF01. Sesiones | Consulta de las sesiones almacenadas en PostgreSQL. |
| RF02. Participantes ficticios | Consulta de participantes con estado `ACTIVO` para el formulario. |
| RF03. Entrada | Registro manual del tipo `ENTRADA`, con validación de datos y duplicados. |
| RF04 / RF07. Persistencia del modelo mínimo | Persistencia JDBC de sesiones, participantes y registros de asistencia. En el acta del PDF aparece como `RF04`; en la matriz de trazabilidad aparece como `RF07`. |

## 2. Tecnologías utilizadas

| Tecnología | Versión o uso |
| --- | --- |
| Java | 11, configurado como versión de compilación |
| Maven | Construcción y empaquetado WAR |
| JSP | Presentación de las vistas web |
| Servlets | Servlet 4.0 con `javax.servlet-api` 4.0.1 |
| Tomcat | 9.0.121 |
| PostgreSQL | Base de datos relacional `pr06_p02` |
| JDBC | Driver PostgreSQL 42.7.2 |

El artefacto Maven del proyecto tiene la versión `1.0-SNAPSHOT` y utiliza empaquetado `war`.

## 3. Requisitos previos

Para ejecutar el proyecto se necesita:

- JDK 11.
- Maven.
- PostgreSQL en ejecución y un usuario con permisos para crear la base y sus tablas.
- Apache Tomcat 9.

VS Code con las extensiones de Maven y Servers fue el entorno utilizado durante el desarrollo, pero no es obligatorio para compilar el proyecto.

## 4. Estructura del proyecto

```text
PR06/
├── .github/
├── Capturas de evidencia/
├── pr06_p02/
│   ├── pom.xml
│   ├── src/main/
│   │   ├── java/com/pr06/asistencia/
│   │   │   ├── model/
│   │   │   ├── dao/
│   │   │   └── servlet/
│   │   └── webapp/
│   │       ├── WEB-INF/web.xml
│   │       └── archivos JSP
│   └── target/pr06_p02.war
├── schema.sql
├── data.sql
└── README.md
```

- `model/`: clases que representan sesiones, participantes y registros.
- `dao/`: acceso a PostgreSQL mediante JDBC y sentencias preparadas.
- `servlet/`: controladores para las peticiones HTTP y el envío de datos a las vistas.
- `webapp/`: páginas JSP y descriptor de despliegue Servlet 4.0.
- `Capturas de evidencia/`: evidencias de ejecución y pruebas manuales.

El directorio `target/` y el WAR se generan durante la compilación.

## 5. Preparación de PostgreSQL

Desde la raíz `PR06/`, abrir el cliente de PostgreSQL:

```bash
psql -U postgres
```

Dentro de `psql`, crear la base, conectarse y ejecutar los scripts:

```sql
CREATE DATABASE pr06_p02;
\connect pr06_p02
\i schema.sql
\i data.sql
```

`schema.sql` crea las entidades `sesion`, `participante`, `registro`, `incidencia`, `dispositivo` y `evento`. El flujo funcional de P02 utiliza principalmente `sesion`, `participante` y `registro`.

El modelo contempla las siguientes entidades:

- `sesion`: evento o sesión donde se registra asistencia.
- `participante`: persona ficticia cuya asistencia será registrada.
- `registro`: entrada o salida asociada a una sesión y a un participante.
- `incidencia`: problema o ajuste asociado a un registro.
- `dispositivo`: fuente de captura prevista para la evolución del proyecto.
- `evento`: evento manual o simulado para trazabilidad futura.

Para comprobar los datos iniciales:

```sql
SELECT * FROM sesion;
SELECT * FROM participante;
```

## 6. Configuración de la conexión

La conexión se encuentra en `pr06_p02/src/main/java/com/pr06/asistencia/dao/ConexionDB.java`. Actualmente utiliza constantes Java y se conecta a:

```text
jdbc:postgresql://localhost:5432/pr06_p02
```

El usuario configurado es `postgres`. Antes de compilar, las credenciales deben ajustarse a la instalación local de PostgreSQL. La configuración conceptual es:

```text
USER = "postgres"
PASSWORD = "TU_CONTRASEÑA"
```

La contraseña local real no se documenta ni debe publicarse en el repositorio. La clase carga el controlador `org.postgresql.Driver` y proporciona las conexiones utilizadas por los DAO.

## 7. Compilación y empaquetado

El proyecto Maven está dentro de `pr06_p02/`. Desde la raíz del repositorio se puede compilar con:

```bash
mvn clean package -f pr06_p02/pom.xml
```

La forma equivalente entrando al módulo es:

```bash
cd pr06_p02
mvn clean package
```

El resultado esperado es:

```text
pr06_p02/target/pr06_p02.war
```

Desde la extensión Maven de VS Code también se utilizaron, en orden, los objetivos `clean`, `validate`, `compile` y `package`.

## 8. Despliegue en Tomcat 9

La aplicación fue probada en Tomcat 9.0.121 con el WAR generado por Maven.

Procedimiento utilizado en VS Code:

1. Compilar y generar `pr06_p02/target/pr06_p02.war`.
2. Iniciar Tomcat 9 desde la vista **Servers**.
3. Seleccionar `pr06_p02.war`.
4. Ejecutar **Run on Server**.
5. Comprobar que el contexto desplegado sea `/pr06_p02`.

Si ya hay una versión desplegada y los cambios no aparecen, se debe retirar esa publicación desde **Servers** y volver a ejecutar **Run on Server** sobre el WAR recién generado.

## 9. Verificación de conexión

Abrir:

```text
http://localhost:8080/pr06_p02/health
```

Resultado esperado:

```text
OK - Conexion a PostgreSQL establecida
```

## 10. Uso del sistema

Recorrido principal:

1. Abrir `http://localhost:8080/pr06_p02/`.
2. Entrar a **Consultar sesiones**.
3. Regresar al inicio o usar el enlace **Registrar asistencia**.
4. Seleccionar una sesión existente.
5. Seleccionar un participante activo.
6. Escribir una observación opcional.
7. Pulsar **Registrar entrada**. El tipo registrado es `ENTRADA`.
8. Entrar a **Consultar registros de asistencia** para verificar la persistencia.

Rutas disponibles:

| Ruta | Propósito |
| --- | --- |
| `/pr06_p02/` | Página principal y navegación. |
| `/pr06_p02/health` | Comprobación de la conexión PostgreSQL. |
| `/pr06_p02/sesiones` | Consulta de sesiones. |
| `/pr06_p02/registro` | Formulario y registro manual de una entrada. |
| `/pr06_p02/registros` | Consulta de registros persistidos. |

## 11. Datos de prueba

`data.sql` contiene datos ficticios para probar el incremento. Entre ellos se encuentran:

- Las sesiones `Sesion de bienvenida` y `Taller de desarrollo web`.
- Los participantes `P001 - Ana Lopez`, `P002 - Carlos Martinez`, `P003 - Laura Hernandez` y `P004 - Miguel Torres`, todos con estado `ACTIVO`.
- El dispositivo ficticio `D001`, que forma parte del modelo de datos pero no del flujo funcional de P02.

El script no inserta registros de asistencia; estos se generan desde `/registro`.

## 12. Pruebas realizadas

| Caso | Tipo | Prueba | Estado | Resultado obtenido |
| --- | --- | --- | --- | --- |
| PP01 | Positiva | Consulta de sesiones | VERIFICADO | Se mostraron correctamente las sesiones almacenadas en la base de datos. |
| PP02 | Positiva | Registro de entrada válido | VERIFICADO | Se mostró `Entrada registrada correctamente.` y el registro fue almacenado. |
| PN01 | Negativa | Entrada duplicada | VERIFICADO | Se rechazó la segunda entrada del mismo participante para la misma sesión y tipo `ENTRADA`. |
| PN02 | Negativa | Sesión inválida | VERIFICADO | Se manipuló `id_sesion = 9999`; el sistema mostró `Debe seleccionar una sesión válida.` y no modificó PostgreSQL. |
| PR01 | Recorrido principal | Registro y comprobación de persistencia | VERIFICADO | El registro apareció en `/registros` y coincidió con la consulta directa `SELECT * FROM registro;`. |

Las evidencias visuales de estas pruebas se encuentran en `Capturas de evidencia/`, incluyendo el documento `PR06_P02.pdf`.

Para comprobar directamente los registros:

```sql
SELECT *
FROM registro
ORDER BY id_registro;
```

Matriz de trazabilidad documentada:

| RF | Implementación | Prueba asociada | Evidencia |
| --- | --- | --- | --- |
| RF01 | `SesionServlet`, `SesionDAO`, `sesiones.jsp` | PP01 | Captura de listado de sesiones. |
| RF02 | `ParticipanteDAO`, `registro.jsp` | PP02 | Selector de participantes. |
| RF03 | `RegistroServlet`, `RegistroDAO` | PP02, PN01, PN02 | Registro válido y validaciones. |
| RF07 | JDBC y PostgreSQL | PR01 | `/registros` y consulta SQL. |

## 13. Flujo principal

```text
Consultar sesiones
    → Seleccionar sesión
    → Seleccionar participante
    → Registrar entrada
    → Persistir en PostgreSQL
    → Mostrar registro realizado
    → Consultar registros persistidos
```

La restricción `UNIQUE (id_sesion, id_participante, tipo_registro)` de PostgreSQL complementa la validación de duplicados realizada por la aplicación.

## 14. Restricciones y alcance

- Se utilizan participantes y datos ficticios.
- No se utilizan datos escolares reales.
- No se recopilan datos biométricos ni geolocalización.
- No existen sanciones automáticas ni integraciones externas.
- P02 se limita al primer incremento Web 1.0.
- El flujo implementado registra solamente `ENTRADA`.
- La salida, incidencias, correcciones, dispositivo y eventos simulados no forman parte del flujo funcional implementado en P02.

Aunque `incidencia`, `dispositivo` y `evento` existen en el modelo SQL, su funcionalidad web todavía no está implementada.

## 15. Estado del proyecto

- Compilación Maven: **VERIFICADO**.
- Generación WAR: **VERIFICADO**.
- Despliegue Tomcat 9: **VERIFICADO**.
- Conexión PostgreSQL: **VERIFICADO**.
- Consulta de sesiones: **VERIFICADO**.
- Registro de entrada: **VERIFICADO**.
- Validaciones: **VERIFICADO**.
- Persistencia: **VERIFICADO**.

## 16. Limitaciones

- El sistema solo registra entradas manuales; no registra salidas.
- No incluye edición ni eliminación de registros.
- No existe autenticación ni administración de usuarios en este incremento.
- La configuración JDBC debe ajustarse manualmente al PostgreSQL local antes de compilar.
- La interfaz JSP es sencilla y está enfocada en demostrar el flujo funcional.
- Las pruebas documentadas son manuales; el repositorio no contiene una suite de pruebas automatizadas para este incremento.

## 17. Incremento P03 — JSF y PrimeFaces

P03 conserva el flujo de P02 y evoluciona su presentación a JSF 2.3 y
PrimeFaces 12: consultar sesiones, registrar una ENTRADA de un participante
ficticio activo y consultar los registros persistidos. No incorpora salidas,
edición, eliminación ni nuevas entidades. Los JSP y servlets de P02 permanecen.
Las secciones anteriores describen P02 y sus verificaciones históricas; los
resultados específicos de P03 se documentan al final de esta sección.

### Tecnologías y requisitos

- Java 11, Maven, WAR y Tomcat 9 (verificación local con Tomcat 9.0.121).
- JSF 2.3: Apache MyFaces API e implementación 2.3.10.
- PrimeFaces 12.0.0, variante `javax` sin clasificador Jakarta.
- JAXB API 2.3.1: MyFaces requiere `DatatypeConverter` para el estado de vista;
  esa clase ya no forma parte del JDK 11.
- PostgreSQL y JDBC existentes, sin cambios al modelo ni a los DAO.

`RegistroBean` utiliza `javax.faces.bean.ManagedBean` y
`javax.faces.bean.ViewScoped`, disponibles aunque deprecados en JSF 2.3.
No requiere un contenedor CDI adicional. El bean y los modelos que almacena
son serializables; las conexiones y los DAO no se almacenan en el estado de vista.

### Base de datos

Desde la raíz del repositorio, únicamente para una instalación nueva:

```bash
psql -h localhost -U postgres -d postgres
```

```sql
CREATE DATABASE pr06_p02;
\connect pr06_p02
\i schema.sql
\i data.sql
SELECT * FROM sesion;
SELECT * FROM participante;
\q
```

Si la base ya está preparada, no repetir los scripts: no son idempotentes.
La conexión sigue configurada en
`pr06_p02/src/main/java/com/pr06/asistencia/dao/ConexionDB.java`.
Conservar las credenciales locales correctas; `.env` no es leído por la aplicación.
P03 no modifica esa clase ni las credenciales del usuario.

### Compilación y despliegue

Desde la raíz:

```bash
java -version
mvn -version
mvn -B -f pr06_p02/pom.xml clean
mvn -B -f pr06_p02/pom.xml compile
mvn -B -f pr06_p02/pom.xml package
```

El artefacto sigue siendo `pr06_p02/target/pr06_p02.war`.
El compilador mantiene `source=11` y `target=11`. Para usar explícitamente el
JDK 11 instalado sin cambiar la configuración global:

```bash
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 mvn -B -f pr06_p02/pom.xml clean package
```

Con Tomcat 9 detenido, copiar el WAR y arrancar:

```bash
export TOMCAT_HOME=/opt/tomcat9
cp pr06_p02/target/pr06_p02.war "$TOMCAT_HOME/webapps/"
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
JRE_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
"$TOMCAT_HOME/bin/catalina.sh" run
```

Ajustar `TOMCAT_HOME` si la instalación está en otra ruta. También se puede
desplegar el WAR con **Run on Server** en Tomcat 9 desde VS Code.

### URLs

- Inicio P03: `http://localhost:8080/pr06_p02/`
- Inicio XHTML explícito: `http://localhost:8080/pr06_p02/index.xhtml`
- Sesiones: `http://localhost:8080/pr06_p02/sesiones.xhtml`
- Formulario: `http://localhost:8080/pr06_p02/registro.xhtml`
- Registros: `http://localhost:8080/pr06_p02/registros.xhtml`
- Conexión: `http://localhost:8080/pr06_p02/health`
- P02 permanece en `/pr06_p02/index.jsp`, `/pr06_p02/sesiones`,
  `/pr06_p02/registro` y `/pr06_p02/registros`.

### Pruebas manuales y evidencia

Antes de registrar, escoger una pareja sesión/participante activo que todavía
no tenga ENTRADA. Se puede localizar con:

```sql
SELECT s.id_sesion, s.nombre, p.id_participante, p.codigo_ficticio
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

1. **P03-01 — Carga:** abrir `sesiones.xhtml`. Deben aparecer las sesiones de
   PostgreSQL en `p:dataTable`. Comparar con `SELECT * FROM sesion;`.
2. **P03-02 — Registro válido:** abrir `registro.xhtml`, elegir una pareja
   disponible, mantener ENTRADA y escribir `Prueba manual P03` como observación.
   Abrir DevTools → Network, limpiar la lista y pulsar **Registrar entrada**.
   Debe aparecer el mensaje de éxito y la fila en la tabla. La petición debe
   ser XHR con `javax.faces.partial.ajax=true`; su respuesta XML debe actualizar
   `registroForm:mensajes` y `registroForm:registros`, sin nueva navegación Document.
3. **P03-03 — Incompleto:** volver a abrir el formulario y enviarlo dejando
   sesión o participante sin seleccionar. JSF debe mostrar el mensaje obligatorio.
   Comparar `SELECT count(*) FROM registro;` antes y después: no debe cambiar.
4. **P03-04 — Duplicado:** seleccionar exactamente la pareja usada en P03-02 y
   enviar de nuevo ENTRADA. Debe aparecer el rechazo de duplicado. Comprobar que
   existe una sola fila para esa pareja y tipo.
5. **P03-05 — Persistencia:** abrir `registros.xhtml` y recargar. La fila de
   P03-02 debe seguir visible y coincidir con la consulta SQL:

```sql
SELECT r.id_registro, s.nombre AS sesion,
       p.codigo_ficticio, r.tipo_registro, r.fecha_hora, r.observacion
FROM registro r
JOIN sesion s ON s.id_sesion = r.id_sesion
JOIN participante p ON p.id_participante = r.id_participante
ORDER BY r.id_registro DESC;

-- Debe devolver cero filas:
SELECT id_sesion, id_participante, tipo_registro, count(*)
FROM registro
GROUP BY id_sesion, id_participante, tipo_registro
HAVING count(*) > 1;
```

Validaciones adicionales del mismo flujo: manipular una sesión/participante
inexistente, enviar tipo SALIDA o una observación de más de 200 caracteres.
El servidor debe rechazarlos sin insertar. El límite también existe en la interfaz.

### Capturas para el reporte

- **Figura 3.1 — Inicio P03:** URL `index.xhtml` y navegación principal.
- **Figura 3.2 — Sesiones:** tabla PrimeFaces con las sesiones de PostgreSQL.
- **Figura 3.3 — Formulario:** selectores, tipo ENTRADA, observación y botón.
- **Figura 3.4 — Campo obligatorio:** mensaje JSF y conteo SQL sin cambios.
- **Figura 3.5 — Registro válido:** mensaje de éxito y fila añadida a la tabla.
- **Figura 3.6 — AJAX:** DevTools con la petición XHR y actualizaciones parciales.
- **Figura 3.7 — Duplicado:** mensaje de rechazo y ausencia de una segunda fila.
- **Figura 3.8 — Persistencia:** registros tras recargar y consulta PostgreSQL
  mostrando el mismo ID, participante, sesión y observación.
- **Figura 3.9 — Maven:** `BUILD SUCCESS`, ruta del WAR y versiones Java/Maven.

### Resultados de verificación P03 (2026-09-22)

Verificación con navegador automatizado, peticiones JSF parciales y consultas
SQL directas. Se utilizó una instancia temporal de Tomcat 9.0.121 en
`http://localhost:18080/pr06_p02/`, con `CATALINA_BASE=/tmp/pr06-p03-tomcat` y
JDK 11.0.33-ea. Esa instancia se detuvo al terminar; no se reemplazó el despliegue
habitual del usuario. Las URLs de uso de arriba asumen Tomcat en el puerto 8080.

| Comprobación | Estado | Evidencia observada |
| --- | --- | --- |
| Maven clean, compile y package | VERIFICADO | BUILD SUCCESS; Maven 3.9.12 ejecutándose con JDK 21.0.13-ea, source/target 11. |
| WAR | VERIFICADO | `pr06_p02/target/pr06_p02.war`, aproximadamente 10 MiB; clases de aplicación con major version 55 (Java 11). |
| Ejecución en Java 11 | VERIFICADO | WAR desplegado y flujo ejecutado en Tomcat 9.0.121 con JDK 11.0.33-ea. |
| P03-01: carga | VERIFICADO | Dos sesiones provenientes de PostgreSQL en la tabla PrimeFaces. |
| P03-02: válido y AJAX | VERIFICADO | Mensaje de éxito, fila visible y persistida; HTTP 200 con respuesta `partial-response` actualizando mensajes y tabla; mismo documento del navegador. |
| P03-03: incompleto | VERIFICADO | Mensajes de sesión y participante obligatorios; conteo PostgreSQL sin cambios. |
| P03-04: duplicado | VERIFICADO | Rechazo de la misma pareja y ENTRADA; sin nueva fila en PostgreSQL. |
| P03-05: persistencia | VERIFICADO | Nueva vista `registros.xhtml` recupera los mismos ID y observaciones confirmados por SQL. |
| Sesión/participante inexistentes, tipo SALIDA y observación de 201 caracteres | VERIFICADO | Peticiones manipuladas rechazadas con mensajes en español; sin inserciones. |
| Rutas P02 y /health | VERIFICADO | HTTP 200 en inicio JSP, sesiones, formulario, registros y comprobación de conexión. |
| Suite automática Maven | NO_VERIFICADO | El repositorio no tiene pruebas automatizadas Maven; Surefire informa `No tests to run`. No equivale a verificar el flujo. |
| Capturas definitivas del reporte | PENDIENTE | Tomar las figuras 3.1–3.9 con el procedimiento anterior. |

Quedaron dos registros ficticios de la verificación: ID 1 (sesión 1,
participante 1, `Verificacion P03 AJAX 2026-09-22`) e ID 3 (sesión 1,
participante 2, `Verificacion final P03`). Para repetir la prueba válida, elegir
otra pareja disponible. La fila temporal ID 2, creada al detectar el truncado,
fue retirada de forma específica durante la prueba; no se reinició la secuencia.

Problemas encontrados y resueltos:

- Falta de `javax.xml.bind.DatatypeConverter` en Java 11: se añadió JAXB API,
  requerida para guardar el estado de vista de MyFaces.
- Mensaje predeterminado en inglés al manipular un selector: validadores JSF
  explícitos mantienen el rechazo y ofrecen mensajes en español.
- `p:inputTextarea` recorta al máximo antes de validar: se comprueba además la
  longitud del parámetro original para rechazar envíos de más de 200 caracteres.
- Advertencia de deprecación de managed beans: se conserva deliberadamente la
  API disponible en JSF 2.3 para este incremento sin añadir CDI.

No se modificaron los DAO, scripts SQL, JSP, servlets ni el cambio local de
`ConexionDB.java`. No se incorporaron funcionalidades fuera del alcance P03.
