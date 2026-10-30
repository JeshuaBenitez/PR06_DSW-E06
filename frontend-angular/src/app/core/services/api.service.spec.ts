import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import {
  Registro,
  RegistroEntrada,
  Salud,
  Sesion
} from '../models/api.models';
import { ApiService } from './api.service';
import { AutenticacionService } from './autenticacion.service';

describe('ApiService', () => {
  let api: ApiService;
  let autenticacion: AutenticacionService;
  let httpTesting: HttpTestingController;

  const salud: Salud = { estado: 'OK', baseDatos: 'DISPONIBLE' };
  const registro: Registro = {
    idRegistro: 1,
    idSesion: 1,
    idParticipante: 4,
    nombreSesion: 'Sesión de prueba',
    nombreParticipante: 'Miguel Torres',
    tipoRegistro: 'ENTRADA',
    fechaHora: '2026-09-15T09:05:30',
    observacion: null
  };
  const sesion: Sesion = {
    idSesion: 1,
    nombre: 'Sesión de prueba',
    fecha: '2026-09-15',
    horaInicio: '09:00:00',
    horaFin: '11:00:00',
    estado: 'EN_CURSO',
    descripcion: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    api = TestBed.inject(ApiService);
    autenticacion = TestBed.inject(AutenticacionService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('consulta salud y conserva la respuesta tipada', () => {
    let respuesta: Salud | undefined;
    api.consultarSalud().subscribe((valor) => (respuesta = valor));

    const request = httpTesting.expectOne('/api/health');
    expect(request.request.method).toBe('GET');
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush(salud);

    expect(respuesta).toEqual(salud);
  });

  it('usa las rutas GET públicas sin enviar Authorization', () => {
    autenticacion.establecerCredenciales('usuario', 'clave');

    api.listarSesiones().subscribe();
    api.listarParticipantesActivos().subscribe();
    api.listarRegistros().subscribe();

    [
      '/api/sesiones',
      '/api/participantes',
      '/api/registros'
    ].forEach((url) => {
      const request = httpTesting.expectOne(url);
      expect(request.request.method).toBe('GET');
      expect(request.request.headers.has('Authorization')).toBeFalse();
      request.flush([]);
    });
  });

  it('permite enviar una operación protegida sin credenciales', () => {
    const solicitud: RegistroEntrada = {
      idSesion: 1,
      idParticipante: 4,
      tipoRegistro: 'ENTRADA'
    };

    api.registrarEntrada(solicitud).subscribe();

    const request = httpTesting.expectOne('/api/registros');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(solicitud);
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush(registro);
  });

  it('envía Basic sólo en POST y PATCH protegidos', () => {
    autenticacion.establecerCredenciales('usuario', 'clave');
    const authorization = `Basic ${btoa('usuario:clave')}`;

    api.registrarEntrada({
      idSesion: 1,
      idParticipante: 4,
      tipoRegistro: 'ENTRADA'
    }).subscribe();
    api.cambiarEstadoSesion(1, { estado: 'EN_CURSO' }).subscribe();

    const post = httpTesting.expectOne('/api/registros');
    expect(post.request.method).toBe('POST');
    expect(post.request.headers.get('Authorization')).toBe(authorization);
    post.flush(registro);

    const patch = httpTesting.expectOne('/api/sesiones/1/estado');
    expect(patch.request.method).toBe('PATCH');
    expect(patch.request.headers.get('Authorization')).toBe(authorization);
    patch.flush(sesion);
  });

  it('no genera Authorization para dominios externos', () => {
    autenticacion.establecerCredenciales('usuario', 'clave');

    expect(
      autenticacion.cabecerasParaOperacionProtegida(
        'https://externo.example/api/registros'
      )
    ).toBeUndefined();
  });

  it('propaga los errores HTTP sin exponer información adicional', () => {
    let estadoHttp = 0;
    api.consultarSalud().subscribe({
      error: (error) => {
        estadoHttp = error.status;
      }
    });

    const request = httpTesting.expectOne('/api/health');
    request.flush(
      {
        codigo: 'SERVICIO_NO_DISPONIBLE',
        mensaje: 'La base de datos no está disponible.'
      },
      { status: 503, statusText: 'Service Unavailable' }
    );

    expect(estadoHttp).toBe(503);
  });
});
