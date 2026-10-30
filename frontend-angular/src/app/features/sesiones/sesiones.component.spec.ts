import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sesion } from '../../core/models/api.models';
import { SesionesComponent } from './sesiones.component';

describe('SesionesComponent', () => {
  let fixture: ComponentFixture<SesionesComponent>;
  let component: SesionesComponent;
  let http: HttpTestingController;

  const programada: Sesion = {
    idSesion: 1,
    nombre: 'Sesión inicial',
    fecha: '2026-10-28',
    horaInicio: '09:00:00',
    horaFin: '10:00:00',
    estado: 'PROGRAMADA',
    descripcion: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [SesionesComponent, HttpClientTestingModule] });
    fixture = TestBed.createComponent(SesionesComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  function iniciar(): void {
    fixture.detectChanges();
  }

  it('muestra carga y después las sesiones', () => {
    iniciar();
    expect(fixture.nativeElement.textContent).toContain('Consultando sesiones');
    http.expectOne('/api/sesiones').flush([programada]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Sesión inicial');
    expect(fixture.nativeElement.textContent).toContain('Iniciar');
  });

  it('muestra el estado vacío', () => {
    iniciar();
    http.expectOne('/api/sesiones').flush([]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No existen sesiones');
  });

  it('muestra un error de conexión controlado', () => {
    iniciar();
    http.expectOne('/api/sesiones').error(new ProgressEvent('error'));
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No fue posible conectar con la API');
  });

  it('aplica una transición válida confirmada', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    iniciar();
    http.expectOne('/api/sesiones').flush([programada]);

    component.cambiarEstado(programada, { estado: 'EN_CURSO', etiqueta: 'Iniciar' });
    const request = http.expectOne('/api/sesiones/1/estado');
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({ estado: 'EN_CURSO' });
    request.flush({ ...programada, estado: 'EN_CURSO' });

    expect(component.sesiones()[0].estado).toBe('EN_CURSO');
  });

  it('impide desde la interfaz una transición no permitida', () => {
    iniciar();
    http.expectOne('/api/sesiones').flush([programada]);
    component.cambiarEstado(programada, { estado: 'FINALIZADA', etiqueta: 'Finalizar' });

    expect(component.mensajeError()).toContain('no está permitida');
    http.expectNone('/api/sesiones/1/estado');
  });
});
