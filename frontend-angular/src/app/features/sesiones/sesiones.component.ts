import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';

import { EstadoSesion, Sesion } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';
import { AutenticacionService } from '../../core/services/autenticacion.service';
import { obtenerMensajeHttp } from '../../core/utils/error-api';

interface Transicion {
  estado: EstadoSesion;
  etiqueta: string;
}

@Component({
  selector: 'app-sesiones',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sesiones.component.html',
  styleUrls: ['./sesiones.component.css']
})
export class SesionesComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly autenticacion = inject(AutenticacionService);

  readonly sesiones = signal<Sesion[]>([]);
  readonly cargando = signal(true);
  readonly mensajeError = signal('');
  readonly mensajeExito = signal('');
  readonly idActualizando = signal<number | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.api.listarSesiones().subscribe({
      next: (sesiones) => {
        this.sesiones.set(sesiones);
        this.cargando.set(false);
      },
      error: (error) => {
        this.mensajeError.set(obtenerMensajeHttp(
          error,
          {},
          'No fue posible consultar las sesiones.'
        ));
        this.cargando.set(false);
      }
    });
  }

  transiciones(sesion: Sesion): Transicion[] {
    if (sesion.estado === 'PROGRAMADA') {
      return [
        { estado: 'EN_CURSO', etiqueta: 'Iniciar' },
        { estado: 'CANCELADA', etiqueta: 'Cancelar' }
      ];
    }
    if (sesion.estado === 'EN_CURSO') {
      return [{ estado: 'FINALIZADA', etiqueta: 'Finalizar' }];
    }
    return [];
  }

  cambiarEstado(sesion: Sesion, transicion: Transicion): void {
    const permitida = this.transiciones(sesion).some(
      (opcion) => opcion.estado === transicion.estado
    );
    if (!permitida) {
      this.mensajeError.set('La transición solicitada no está permitida.');
      return;
    }

    const confirmado = window.confirm(
      `¿Cambiar la sesión "${sesion.nombre}" a ${transicion.estado}?`
    );
    if (!confirmado) {
      return;
    }

    this.idActualizando.set(sesion.idSesion);
    this.mensajeError.set('');
    this.mensajeExito.set('');
    this.api.cambiarEstadoSesion(sesion.idSesion, { estado: transicion.estado }).subscribe({
      next: (actualizada) => {
        this.sesiones.update((actuales) =>
          actuales.map((item) => item.idSesion === actualizada.idSesion ? actualizada : item)
        );
        this.autenticacion.marcarOperacionAceptada();
        this.mensajeExito.set(`La sesión cambió a ${actualizada.estado}.`);
        this.idActualizando.set(null);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.autenticacion.marcarOperacionRechazada();
        }
        this.mensajeError.set(obtenerMensajeHttp(error, {
          400: 'El estado o identificador enviado no es válido.',
          401: 'Se requieren credenciales válidas para cambiar el estado.',
          404: 'La sesión ya no existe.',
          409: 'El backend rechazó la transición de estado.'
        }, 'No fue posible cambiar el estado de la sesión.'));
        this.idActualizando.set(null);
      }
    });
  }
}
