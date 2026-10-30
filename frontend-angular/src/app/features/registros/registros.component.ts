import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';

import { Registro } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';
import { obtenerMensajeHttp } from '../../core/utils/error-api';

@Component({
  selector: 'app-registros',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './registros.component.html'
})
export class RegistrosComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly registros = signal<Registro[]>([]);
  readonly cargando = signal(true);
  readonly mensajeError = signal('');

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.api.listarRegistros().subscribe({
      next: (registros) => {
        this.registros.set(registros);
        this.cargando.set(false);
      },
      error: (error) => {
        this.mensajeError.set(obtenerMensajeHttp(
          error,
          {},
          'No fue posible consultar los registros.'
        ));
        this.cargando.set(false);
      }
    });
  }
}
