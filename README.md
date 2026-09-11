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
