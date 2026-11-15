import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription, catchError, interval, of, startWith, switchMap } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { LecturaIoT } from '../../core/models/api.models';

@Component({
  selector: 'app-telemetria', standalone: true, imports: [CommonModule, FormsModule],
  templateUrl: './telemetria.component.html', styleUrls: ['./telemetria.component.css']
})
export class TelemetriaComponent implements OnInit, OnDestroy {
  private readonly api = inject(ApiService);
  private ciclo?: Subscription;
  sesionId = 1;
  readonly lecturas = signal<LecturaIoT[]>([]);
  readonly error = signal('');
  readonly ultima = computed(() => this.lecturas()[0] ?? null);
  readonly desconectado = computed(() => {
    const fecha = this.ultima()?.fechaHora;
    return !fecha || Date.now() - Date.parse(fecha) > 30000;
  });
  ngOnInit(): void { this.iniciar(); }
  ngOnDestroy(): void { this.ciclo?.unsubscribe(); }
  iniciar(): void {
    this.ciclo?.unsubscribe();
    this.lecturas.set([]);
    this.ciclo = interval(5000).pipe(startWith(0), switchMap(() => this.api.listarTelemetria(this.sesionId).pipe(
      catchError(() => { this.error.set('No se pudo consultar la telemetría. Comprueba la API.'); return of(null); })
    )))
      .subscribe({
        next: datos => { if (datos) { this.lecturas.set(datos); this.error.set(''); } }
      });
  }
}
