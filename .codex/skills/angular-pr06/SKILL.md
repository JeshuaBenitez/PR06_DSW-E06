---
name: angular-pr06
description: "Implementa o revisa exclusivamente el frontend P05 bajo frontend-angular/** con Angular 16.2.12; no se aplica al legado pr06_p02/**."
---

# Angular P05

Aplica esta skill sólo a tareas dentro de `frontend-angular/**`.

## Compatibilidad

- Usa Angular 16.2.12.
- Prefiere componentes standalone y declara explícitamente sus imports.
- Usa `signal()` y `computed()` para estado local sencillo.
- Usa RxJS y `HttpClient` para operaciones HTTP y flujos asíncronos; no sustituyas automáticamente los `Observable` por Signals.
- Usa `*ngIf`, `*ngFor` y `ngSwitch`. No uses `@if`, `@for` ni `@switch`, porque no son compatibles con Angular 16.
- Define servicios tipados con `providedIn: 'root'` e interfaces para los contratos HTTP.

## Límites

- No modifiques `pr06_p02/**`.
- No crees ni actualices el proyecto Angular fuera de `frontend-angular/**`.
- Conserva el alcance funcional heredado y el contrato aprobado; no inventes endpoints.
- Mantén el código legible para fines educativos y explica brevemente las decisiones que no sean evidentes.
- Verifica las pruebas disponibles del frontend y reporta con precisión lo no ejecutado.
