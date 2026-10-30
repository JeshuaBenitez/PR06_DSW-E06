import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutenticacionService } from '../../core/services/autenticacion.service';
import { CredencialesComponent } from './credenciales.component';

describe('CredencialesComponent', () => {
  let fixture: ComponentFixture<CredencialesComponent>;
  let component: CredencialesComponent;
  let autenticacion: AutenticacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [CredencialesComponent] });
    fixture = TestBed.createComponent(CredencialesComponent);
    component = fixture.componentInstance;
    autenticacion = TestBed.inject(AutenticacionService);
  });

  it('guarda credenciales como pendientes, sin afirmar autenticación', () => {
    component.usuario = 'docente';
    component.password = 'temporal';
    component.guardar();

    expect(autenticacion.tieneCredenciales()).toBeTrue();
    expect(autenticacion.estadoCredenciales()).toBe('pendiente');
    expect(component.password).toBe('');
  });

  it('elimina las credenciales de memoria', () => {
    autenticacion.establecerCredenciales('docente', 'temporal');
    component.borrar();

    expect(autenticacion.tieneCredenciales()).toBeFalse();
    expect(autenticacion.estadoCredenciales()).toBe('ausentes');
  });
});
