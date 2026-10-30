import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';

import { Salud } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

type EstadoConsulta = 'cargando' | 'exito' | 'error';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css']
})
export class InicioComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly estadoConsulta = signal<EstadoConsulta>('cargando');
  readonly salud = signal<Salud | null>(null);
  readonly mensajeError = signal('');

  ngOnInit(): void {
    this.consultarSalud();
  }

  consultarSalud(): void {
    this.estadoConsulta.set('cargando');
    this.salud.set(null);
    this.mensajeError.set('');

    this.api.consultarSalud().subscribe({
      next: (respuesta) => {
        this.salud.set(respuesta);
        this.estadoConsulta.set('exito');
      },
      error: () => {
        this.mensajeError.set(
          'No fue posible consultar la disponibilidad del sistema.'
        );
        this.estadoConsulta.set('error');
      }
    });
  }
}
