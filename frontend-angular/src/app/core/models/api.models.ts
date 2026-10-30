export type EstadoSesion =
  | 'PROGRAMADA'
  | 'EN_CURSO'
  | 'FINALIZADA'
  | 'CANCELADA';

export interface Sesion {
  idSesion: number;
  nombre: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  estado: EstadoSesion;
  descripcion: string | null;
}

export interface Participante {
  idParticipante: number;
  codigoFicticio: string;
  nombre: string;
  apellido: string;
  estado: 'ACTIVO';
}

export interface Registro {
  idRegistro: number;
  idSesion: number;
  idParticipante: number;
  nombreSesion: string;
  nombreParticipante: string;
  tipoRegistro: 'ENTRADA';
  fechaHora: string;
  observacion: string | null;
}

export interface RegistroEntrada {
  idSesion: number;
  idParticipante: number;
  tipoRegistro: 'ENTRADA';
  observacion?: string | null;
}

export interface CambioEstadoSesion {
  estado: EstadoSesion;
}

export interface Salud {
  estado: 'OK';
  baseDatos: 'DISPONIBLE';
}

export type CodigoErrorApi =
  | 'DATOS_INVALIDOS'
  | 'NO_AUTORIZADO'
  | 'REFERENCIA_NO_ENCONTRADA'
  | 'REGISTRO_DUPLICADO'
  | 'TRANSICION_NO_PERMITIDA'
  | 'SERVICIO_NO_DISPONIBLE'
  | 'ERROR_INTERNO';

export interface ErrorApi {
  codigo: CodigoErrorApi;
  mensaje: string;
}
