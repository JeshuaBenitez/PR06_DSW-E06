import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicioComponent } from './inicio.component';

describe('InicioComponent', () => {
  let fixture: ComponentFixture<InicioComponent>;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InicioComponent, HttpClientTestingModule]
    });
    fixture = TestBed.createComponent(InicioComponent);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('muestra carga y después la disponibilidad de API y PostgreSQL', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain(
      'Consultando el backend'
    );

    const request = httpTesting.expectOne('/api/health');
    request.flush({ estado: 'OK', baseDatos: 'DISPONIBLE' });
    fixture.detectChanges();

    const contenido = fixture.nativeElement.textContent as string;
    expect(contenido).toContain('Backend disponible');
    expect(contenido).toContain('OK');
    expect(contenido).toContain('DISPONIBLE');
  });

  it('muestra un error controlado y permite reintentar', () => {
    fixture.detectChanges();
    httpTesting.expectOne('/api/health').flush(
      { codigo: 'SERVICIO_NO_DISPONIBLE', mensaje: 'No disponible' },
      { status: 503, statusText: 'Service Unavailable' }
    );
    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelector('[role="alert"]')?.textContent).toContain(
      'No fue posible consultar la disponibilidad del sistema.'
    );

    elemento.querySelector('button')?.click();
    fixture.detectChanges();
    httpTesting.expectOne('/api/health').flush({
      estado: 'OK',
      baseDatos: 'DISPONIBLE'
    });
  });
});
