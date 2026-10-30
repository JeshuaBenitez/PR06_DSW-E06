import { HttpErrorResponse } from '@angular/common/http';

export interface MensajesHttp {
  [estado: number]: string;
}

export function obtenerMensajeHttp(
  error: unknown,
  mensajes: MensajesHttp,
  mensajePredeterminado: string
): string {
  if (!(error instanceof HttpErrorResponse)) {
    return mensajePredeterminado;
  }

  if (error.status === 0) {
    return 'No fue posible conectar con la API.';
  }

  return mensajes[error.status] ?? mensajePredeterminado;
}
