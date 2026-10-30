import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Participante, Registro, Sesion } from '../../core/models/api.models';
import { AutenticacionService } from '../../core/services/autenticacion.service';
import { RegistrarComponent } from './registrar.component';

describe('RegistrarComponent', () => {
  let fixture: ComponentFixture<RegistrarComponent>;
  let component: RegistrarComponent;
  let http: HttpTestingController;
  let autenticacion: AutenticacionService;

  const sesion: Sesion = {
    idSesion: 1,
    nombre: 'Sesión inicial',
    fecha: '2026-10-28',
    horaInicio: '09:00:00',
    horaFin: '10:00:00',
    estado: 'PROGRAMADA',
    descripcion: null
  };
  const participante: Participante = {
    idParticipante: 2,
    codigoFicticio: 'P002',
    nombre: 'Ana',
    apellido: 'López',
    estado: 'ACTIVO'
  };
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
    TestBed.configureTestingModule({ imports: [RegistrarComponent, HttpClientTestingModule] });
    fixture = TestBed.createComponent(RegistrarComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    autenticacion = TestBed.inject(AutenticacionService);
  });

  afterEach(() => http.verify());

  function cargarCatalogos(): void {
    fixture.detectChanges();
    http.expectOne('/api/sesiones').flush([sesion]);
    http.expectOne('/api/participantes').flush([participante]);
    fixture.detectChanges();
  }

  function completarFormulario(): void {
    component.formulario.setValue({
      idSesion: 1,
      idParticipante: 2,
      observacion: ''
    });
  }

  it('carga sesiones y participantes activos', () => {
    cargarCatalogos();
    expect(component.sesiones()).toEqual([sesion]);
    expect(component.participantes()).toEqual([participante]);
  });

  it('rechaza un formulario incompleto sin enviar POST', () => {
    cargarCatalogos();
    component.registrar();
    expect(component.formulario.invalid).toBeTrue();
    http.expectNone('/api/registros');
  });

  it('valida la observación superior a 200 caracteres', () => {
    cargarCatalogos();
    component.formulario.controls.observacion.setValue('x'.repeat(201));
    expect(component.formulario.controls.observacion.hasError('maxlength')).toBeTrue();
    component.registrar();
    http.expectNone('/api/registros');
  });

  it('evita un segundo envío mientras el primero está pendiente', () => {
    cargarCatalogos();
    completarFormulario();
    component.registrar();
    component.registrar();

    const requests = http.match('/api/registros');
    expect(requests.length).toBe(1);
    requests[0].flush(registro);
  });

  it('muestra el registro exitoso y marca credenciales aceptadas', () => {
    autenticacion.establecerCredenciales('docente', 'temporal');
    cargarCatalogos();
    completarFormulario();
    component.registrar();
    const request = http.expectOne('/api/registros');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.tipoRegistro).toBe('ENTRADA');
    request.flush(registro);

    expect(component.registroCreado()).toEqual(registro);
    expect(autenticacion.estadoCredenciales()).toBe('aceptada');
  });

  it('informa 401 y marca las credenciales como rechazadas', () => {
    autenticacion.establecerCredenciales('docente', 'incorrecta');
    cargarCatalogos();
    completarFormulario();
    component.registrar();
    http.expectOne('/api/registros').flush(
      { codigo: 'NO_AUTORIZADO', mensaje: 'No autorizado' },
      { status: 401, statusText: 'Unauthorized' }
    );

    expect(component.mensajeError()).toContain('credenciales válidas');
    expect(autenticacion.estadoCredenciales()).toBe('rechazada');
  });

  it('informa un registro duplicado con HTTP 409', () => {
    cargarCatalogos();
    completarFormulario();
    component.registrar();
    http.expectOne('/api/registros').flush(
      { codigo: 'REGISTRO_DUPLICADO', mensaje: 'Duplicado' },
      { status: 409, statusText: 'Conflict' }
    );

    expect(component.mensajeError()).toContain('ya tiene una entrada');
  });

  it('maneja un error de conexión', () => {
    cargarCatalogos();
    completarFormulario();
    component.registrar();
    http.expectOne('/api/registros').error(new ProgressEvent('error'));
    expect(component.mensajeError()).toContain('conectar con la API');
  });
});
