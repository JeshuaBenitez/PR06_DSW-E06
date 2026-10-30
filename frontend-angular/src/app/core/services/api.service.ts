import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import {
  CambioEstadoSesion,
  Participante,
  Registro,
  RegistroEntrada,
  Salud,
  Sesion
} from '../models/api.models';
import { AutenticacionService } from './autenticacion.service';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly autenticacion = inject(AutenticacionService);

  consultarSalud(): Observable<Salud> {
    return this.http.get<Salud>(`${API_BASE_URL}/health`);
  }

  listarSesiones(): Observable<Sesion[]> {
    return this.http.get<Sesion[]>(`${API_BASE_URL}/sesiones`);
  }

  listarParticipantesActivos(): Observable<Participante[]> {
    return this.http.get<Participante[]>(`${API_BASE_URL}/participantes`);
  }

  listarRegistros(): Observable<Registro[]> {
    return this.http.get<Registro[]>(`${API_BASE_URL}/registros`);
  }

  registrarEntrada(solicitud: RegistroEntrada): Observable<Registro> {
    const url = `${API_BASE_URL}/registros`;
    return this.http.post<Registro>(
      url,
      solicitud,
      this.opcionesProtegidas(url)
    );
  }

  cambiarEstadoSesion(
    idSesion: number,
    solicitud: CambioEstadoSesion
  ): Observable<Sesion> {
    const url = `${API_BASE_URL}/sesiones/${idSesion}/estado`;
    return this.http.patch<Sesion>(
      url,
      solicitud,
      this.opcionesProtegidas(url)
    );
  }

  private opcionesProtegidas(url: string): { headers?: HttpHeaders } {
    const headers = this.autenticacion.cabecerasParaOperacionProtegida(url);
    return headers ? { headers } : {};
  }
}
