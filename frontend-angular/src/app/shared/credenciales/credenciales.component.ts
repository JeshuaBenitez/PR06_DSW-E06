import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AutenticacionService } from '../../core/services/autenticacion.service';

@Component({
  selector: 'app-credenciales',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './credenciales.component.html',
  styleUrls: ['./credenciales.component.css']
})
export class CredencialesComponent {
  readonly autenticacion = inject(AutenticacionService);

  usuario = '';
  password = '';
  mensajeFormulario = '';

  guardar(): void {
    if (!this.usuario.trim() || !this.password) {
      this.mensajeFormulario = 'Capture usuario y contraseña.';
      return;
    }

    this.autenticacion.establecerCredenciales(this.usuario.trim(), this.password);
    this.password = '';
    this.mensajeFormulario = '';
  }

  borrar(): void {
    this.autenticacion.limpiarCredenciales();
    this.usuario = '';
    this.password = '';
    this.mensajeFormulario = '';
  }
}
