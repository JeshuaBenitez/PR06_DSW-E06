import { HttpHeaders } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';

import { API_BASE_URL } from '../config/api.config';

interface CredencialesBasic {
  usuario: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AutenticacionService {
  private readonly credenciales = signal<CredencialesBasic | null>(null);
  private readonly validacion = signal<'pendiente' | 'aceptada' | 'rechazada' | null>(null);

  readonly tieneCredenciales = computed(() => this.credenciales() !== null);
  readonly estadoCredenciales = computed(() => {
    if (!this.credenciales()) {
      return 'ausentes' as const;
    }
    return this.validacion() ?? 'pendiente';
  });

  establecerCredenciales(usuario: string, password: string): void {
    this.credenciales.set({ usuario, password });
    this.validacion.set('pendiente');
  }

  limpiarCredenciales(): void {
    this.credenciales.set(null);
    this.validacion.set(null);
  }

  marcarOperacionAceptada(): void {
    if (this.credenciales()) {
      this.validacion.set('aceptada');
    }
  }

  marcarOperacionRechazada(): void {
    if (this.credenciales()) {
      this.validacion.set('rechazada');
    }
  }

  cabecerasParaOperacionProtegida(url: string): HttpHeaders | undefined {
    const actuales = this.credenciales();
    if (!actuales || !this.esUrlLocalDeApi(url)) {
      return undefined;
    }

    const valor = `${actuales.usuario}:${actuales.password}`;
    return new HttpHeaders({
      Authorization: `Basic ${this.codificarBase64(valor)}`
    });
  }

  private esUrlLocalDeApi(url: string): boolean {
    return url === API_BASE_URL || url.startsWith(`${API_BASE_URL}/`);
  }

  private codificarBase64(valor: string): string {
    const bytes = new TextEncoder().encode(valor);
    let binario = '';
    bytes.forEach((byte) => {
      binario += String.fromCharCode(byte);
    });
    return btoa(binario);
  }
}
