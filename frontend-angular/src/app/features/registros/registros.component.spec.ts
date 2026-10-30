import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Registro } from '../../core/models/api.models';
import { RegistrosComponent } from './registros.component';

describe('RegistrosComponent', () => {
  let fixture: ComponentFixture<RegistrosComponent>;
  let http: HttpTestingController;

  const registro: Registro = {
    idRegistro: 8,
    idSesion: 1,
    idParticipante: 2,
    nombreSesion: 'Sesión inicial',
    nombreParticipante: 'Ana López',
    tipoRegistro: 'ENTRADA',
    fechaHora: '2026-10-28T09:05:00',
    observacion: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [RegistrosComponent, HttpClientTestingModule] });
    fixture = TestBed.createComponent(RegistrosComponent);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('consulta y muestra registros persistidos', () => {
    fixture.detectChanges();
    http.expectOne('/api/registros').flush([registro]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Ana López');
    expect(fixture.nativeElement.textContent).toContain('Sesión inicial');
  });

  it('muestra una lista vacía', () => {
    fixture.detectChanges();
    http.expectOne('/api/registros').flush([]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No existen registros');
  });

  it('muestra errores de consulta', () => {
    fixture.detectChanges();
    http.expectOne('/api/registros').flush({}, { status: 500, statusText: 'Error' });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No fue posible consultar');
  });
});
