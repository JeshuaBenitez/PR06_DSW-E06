import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Participante, Registro, Sesion } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';
import { AutenticacionService } from '../../core/services/autenticacion.service';
import { obtenerMensajeHttp } from '../../core/utils/error-api';

@Component({
  selector: 'app-registrar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registrar.component.html',
  styleUrls: ['./registrar.component.css']
})
export class RegistrarComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly autenticacion = inject(AutenticacionService);

  readonly sesiones = signal<Sesion[]>([]);
  readonly participantes = signal<Participante[]>([]);
  readonly cargandoCatalogos = signal(true);
  readonly enviando = signal(false);
  readonly mensajeError = signal('');
  readonly registroCreado = signal<Registro | null>(null);

  readonly formulario = new FormGroup({
    idSesion: new FormControl<number | null>(null, Validators.required),
    idParticipante: new FormControl<number | null>(null, Validators.required),
    observacion: new FormControl('', Validators.maxLength(200))
  });

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.cargandoCatalogos.set(true);
    this.mensajeError.set('');
    forkJoin({
      sesiones: this.api.listarSesiones(),
      participantes: this.api.listarParticipantesActivos()
    }).subscribe({
      next: ({ sesiones, participantes }) => {
        this.sesiones.set(sesiones);
        this.participantes.set(participantes);
        this.cargandoCatalogos.set(false);
      },
      error: (error) => {
        this.mensajeError.set(obtenerMensajeHttp(
          error,
          {},
          'No fue posible cargar sesiones y participantes.'
        ));
        this.cargandoCatalogos.set(false);
      }
    });
  }

  registrar(): void {
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid || this.enviando()) {
      return;
    }

    const valor = this.formulario.getRawValue();
    if (valor.idSesion === null || valor.idParticipante === null) {
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set('');
    this.registroCreado.set(null);

    this.api.registrarEntrada({
      idSesion: valor.idSesion,
      idParticipante: valor.idParticipante,
      tipoRegistro: 'ENTRADA',
      observacion: valor.observacion === '' ? null : valor.observacion
    }).subscribe({
      next: (registro) => {
        this.registroCreado.set(registro);
        this.autenticacion.marcarOperacionAceptada();
        this.formulario.reset({ idSesion: null, idParticipante: null, observacion: '' });
        this.enviando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.autenticacion.marcarOperacionRechazada();
        }
        this.mensajeError.set(obtenerMensajeHttp(error, {
          400: 'Los datos enviados no son válidos.',
          401: 'Se requieren credenciales válidas para registrar la entrada.',
          404: 'La sesión o el participante ya no existe.',
          409: 'El participante ya tiene una entrada en esta sesión.'
        }, 'No fue posible registrar la entrada.'));
        this.enviando.set(false);
      }
    });
  }
}
