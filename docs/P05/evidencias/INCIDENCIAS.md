# Incidencias reales de la ejecución

1. El primer `rsync` del respaldo se detuvo porque no existía el directorio
   padre `.codex` en el destino. Se creó el padre y se completó el mismo
   respaldo; no se perdió ni sobrescribió información.
2. El primer runner CDP evaluó la página durante el cambio de contexto de
   navegación y terminó con `Uncaught`. Se hizo tolerante a la destrucción
   temporal del contexto. No alcanzó operaciones de escritura.
3. Los primeros procesos en segundo plano terminaron al finalizar su shell. Se
   sustituyeron por una sesión persistente con limpieza controlada. No hubo
   escritura en esos intentos.
4. El recorrido principal completó el HTTP 201 y el duplicado 409, pero Chrome
   dejó pendiente el POST sin credenciales por el desafío HTTP Basic. El runner
   se interrumpió y se reanudó sin repetir el alta positiva, cancelando el reto
   mediante `Fetch.authRequired`; así se obtuvo el HTTP 401 real.
5. Algunas capturas iniciales mostraban el usuario Basic efímero. Se
   recapturaron con campos vacíos. La captura inmediata del 201 se eliminó y se
   sustituyó por evidencia HTTP, consulta Angular y SQL saneadas.

El archivo `02-http-error.txt` conserva el error del intento intermedio. Ninguna
incidencia produjo escrituras adicionales: SQL final muestra una sola alta
positiva nueva.

