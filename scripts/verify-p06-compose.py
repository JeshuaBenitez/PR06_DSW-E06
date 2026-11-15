#!/usr/bin/env python3
"""Comprueba P06 en los tres servicios Docker Compose y retira solo su proyecto temporal."""
import base64
import datetime as dt
import json
from pathlib import Path
import secrets
import socket
import subprocess
import tempfile
import time
import urllib.error
import urllib.request
import uuid

ROOT = Path(__file__).resolve().parents[1]
EVIDENCE = ROOT / 'docs/P06/evidencia'
EVIDENCE.mkdir(parents=True, exist_ok=True)
PROJECT = 'pr06-p06-compose-' + uuid.uuid4().hex[:10]
PORTS = {'PR06_FRONTEND_PORT': 4306, 'PR06_BACKEND_PORT': 18086, 'PR06_DB_PORT': 55436}
DB_NAME = 'pr06_p06_compose_verify'
DB_USER = 'pr06_verify'
API_USER = 'p06_verificador'
DB_PASSWORD = secrets.token_urlsafe(20)
API_PASSWORD = secrets.token_urlsafe(20)

for port in PORTS.values():
    with socket.socket() as sock:
        try:
            sock.bind(('127.0.0.1', port))
        except OSError as error:
            raise SystemExit(f'Puerto local {port} ocupado; no se inició Compose') from error

with tempfile.TemporaryDirectory(prefix='pr06-p06-compose-') as tmp_dir:
    tmp = Path(tmp_dir)
    env_file = tmp / 'compose.env'
    values = {**PORTS, 'PR06_DOCKER_DB_NAME': DB_NAME, 'PR06_DOCKER_DB_USER': DB_USER,
        'PR06_DOCKER_DB_PASSWORD': DB_PASSWORD, 'PR06_DOCKER_BASIC_USER': API_USER,
        'PR06_DOCKER_BASIC_PASSWORD': API_PASSWORD}
    env_file.write_text(''.join(f'{key}={value}\n' for key, value in values.items()))
    env_file.chmod(0o600)
    compose = ['docker', 'compose', '-p', PROJECT, '--env-file', str(env_file)]
    def run(args, **kwargs):
        return subprocess.run([str(x) for x in args], cwd=ROOT, check=True, **kwargs)

    def http(method, path, body=None, authenticated=False):
        headers = {'Content-Type': 'application/json'}
        if authenticated:
            encoded = base64.b64encode(f'{API_USER}:{API_PASSWORD}'.encode()).decode()
            headers['Authorization'] = 'Basic ' + encoded
        request = urllib.request.Request('http://127.0.0.1:4306' + path,
            data=json.dumps(body).encode() if body is not None else None,
            headers=headers, method=method)
        try:
            with urllib.request.urlopen(request, timeout=8) as response:
                return response.status, json.load(response)
        except urllib.error.HTTPError as error:
            return error.code, json.load(error)

    try:
        with (tmp / 'build.log').open('w') as build_log:
            run(compose + ['up', '-d', '--build'], stdout=build_log, stderr=subprocess.STDOUT)
        for _ in range(100):
            try:
                health = http('GET', '/api/health')
                if health[0] == 200:
                    break
            except (urllib.error.URLError, TimeoutError, ConnectionResetError,
                    json.JSONDecodeError):
                pass
            time.sleep(1)
        else:
            raise RuntimeError('Frontend/API no alcanzaron salud HTTP 200')

        # La petición entra por Nginx y cruza API, reglas, JPA/JDBC y PostgreSQL.
        lectura = {'idDispositivo': 1, 'idSesion': 1,
            'fechaHora': dt.datetime.now(dt.timezone.utc).isoformat(),
            'variable': 'AFORO', 'unidad': 'personas', 'valor': 26, 'origen': 'SIMULADOR_HTTP'}
        sin_clave = http('POST', '/api/telemetria', lectura)
        invalida = http('POST', '/api/telemetria', {**lectura, 'valor': 101}, True)
        creada = http('POST', '/api/telemetria', lectura, True)
        historial = http('GET', '/api/telemetria?sesionId=1')
        sql = run(compose + ['exec', '-T', 'db', 'psql', '-U', DB_USER, '-d', DB_NAME,
            '-tAc', 'SELECT COUNT(*) FROM lectura_iot WHERE id_sesion=1'],
            capture_output=True, text=True).stdout.strip()
        version = run(compose + ['exec', '-T', 'db', 'psql', '-U', DB_USER, '-d', DB_NAME,
            '-tAc', 'SHOW server_version'], capture_output=True, text=True).stdout.strip()
        assert sin_clave[0] == 401 and invalida[0] == 400 and creada[0] == 201
        assert historial[0] == 200 and len(historial[1]) == 1 and int(sql) == 1
        assert creada[1]['alerta'] == 'AFORO_ALTO' and version.startswith('16.')

        run(['google-chrome', '--headless', '--no-sandbox', '--disable-gpu',
            '--disable-dev-shm-usage', '--hide-scrollbars', f'--user-data-dir={tmp}/chrome',
            '--window-size=1440,1000', '--virtual-time-budget=7000',
            f'--screenshot={EVIDENCE}/P06_EQUIPO_DSW-E06-compose.png',
            'http://127.0.0.1:4306/telemetria'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        result = {'modo': 'Docker Compose completo', 'servicios': ['frontend', 'backend', 'db'],
            'postgresVersion': version, 'salud': health, 'sinAutenticacion': sin_clave[0],
            'valorFueraDeRango': invalida[0], 'lecturaValida': creada,
            'historial': historial, 'filasPostgreSQL': int(sql),
            'captura': 'P06_EQUIPO_DSW-E06-compose.png'}
        (EVIDENCE / 'P06_EQUIPO_DSW-E06-compose.json').write_text(
            json.dumps(result, ensure_ascii=False, indent=2))
        print(f'VERIFICADO Compose: frontend, API, PostgreSQL {version}, 401/400/201/200 y SQL')
    except (subprocess.CalledProcessError, RuntimeError):
        build_log = tmp / 'build.log'
        if build_log.exists():
            print('Últimas líneas del build Compose:')
            print('\n'.join(build_log.read_text(errors='replace').splitlines()[-35:]))
        logs = subprocess.run(compose + ['logs', '--no-color', '--tail', '25'],
            cwd=ROOT, capture_output=True, text=True)
        if logs.returncode == 0:
            print('Últimas líneas de servicios Compose:')
            print(logs.stdout[-4000:])
        raise
    finally:
        run(compose + ['down', '-v', '--remove-orphans'],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
