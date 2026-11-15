#!/usr/bin/env python3
"""Recorrido aislado P06. Crea una base temporal y conserva evidencia saneada."""
import base64
import json
import os
from pathlib import Path
import secrets
import subprocess
import tempfile
import time
import urllib.error
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
EVIDENCE = ROOT / 'docs/P06/evidencia'
EVIDENCE.mkdir(parents=True, exist_ok=True)
PG_BIN = Path(os.getenv('PG_BIN', '/usr/lib/postgresql/17/bin'))
pg_port = '55439'
api_port = '8080'
user = 'p06_verificador'
password = secrets.token_urlsafe(20)
auth = 'Basic ' + base64.b64encode(f'{user}:{password}'.encode()).decode()
children = []

def run(args, **kwargs):
    subprocess.run([str(a) for a in args], check=True, cwd=ROOT, **kwargs)

def request(method, path, body=None, authenticated=False):
    headers = {'Content-Type': 'application/json'}
    if authenticated:
        headers['Authorization'] = auth
    req = urllib.request.Request('http://127.0.0.1:8080/api' + path,
        data=json.dumps(body).encode() if body is not None else None,
        headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status, json.load(response)
    except urllib.error.HTTPError as error:
        return error.code, json.load(error)

def wait_url(url, tries=80):
    for _ in range(tries):
        try:
            with urllib.request.urlopen(url, timeout=2):
                return
        except Exception:
            time.sleep(.5)
    raise RuntimeError('Servicio no disponible: ' + url)

with tempfile.TemporaryDirectory(prefix='pr06-p06-') as tmp:
    tmp = Path(tmp)
    try:
        run([PG_BIN/'initdb', '-D', tmp/'pg', '-A', 'trust', '--no-instructions'], stdout=subprocess.DEVNULL)
        run([PG_BIN/'pg_ctl', '-D', tmp/'pg', '-o', f'-h 127.0.0.1 -p {pg_port} -k {tmp}',
             '-l', tmp/'postgres.log', 'start'], stdout=subprocess.DEVNULL)
        run(['createdb', '-h', '127.0.0.1', '-p', pg_port, 'pr06_p06_verify'])
        for sql in ['schema.sql', 'data.sql', 'docs/P06/01-telemetria.sql']:
            run(['psql', '-h', '127.0.0.1', '-p', pg_port, '-d', 'pr06_p06_verify',
                 '-v', 'ON_ERROR_STOP=1', '-q', '-f', ROOT/sql], stdout=subprocess.DEVNULL)
        env = os.environ.copy()
        env.update(DB_URL=f'jdbc:postgresql://127.0.0.1:{pg_port}/pr06_p06_verify',
                   DB_USER=os.getenv('USER', 'sinoe'), DB_PASSWORD='',
                   API_BASIC_USER=user, API_BASIC_PASSWORD=password, SERVER_PORT=api_port)
        api_log = open(tmp/'api.log', 'w')
        children.append(subprocess.Popen(['/usr/lib/jvm/java-11-openjdk-amd64/bin/java', '-jar',
            str(ROOT/'api-spring/target/api-spring-0.0.1-SNAPSHOT.jar')], env=env, stdout=api_log, stderr=subprocess.STDOUT))
        wait_url('http://127.0.0.1:8080/api/health')

        import datetime as dt
        dato = dict(idDispositivo=1, idSesion=1, fechaHora=dt.datetime.now(dt.timezone.utc).isoformat(),
                    variable='AFORO', unidad='personas', valor=26, origen='SIMULADOR_HTTP')
        no_auth = request('POST', '/telemetria', dato)
        invalido = request('POST', '/telemetria', {**dato, 'valor': 101}, True)
        valido = request('POST', '/telemetria', dato, True)
        historial = request('GET', '/telemetria?sesionId=1')
        sql = subprocess.check_output(['psql', '-h', '127.0.0.1', '-p', pg_port, '-d', 'pr06_p06_verify',
            '-tAc', 'SELECT COUNT(*) FROM lectura_iot WHERE id_sesion=1'], text=True).strip()
        resultado = {'sinAutenticacion': no_auth[0], 'valorFueraDeRango': invalido[0],
                     'lecturaValida': valido, 'historial': historial, 'filasPostgreSQL': int(sql)}
        (EVIDENCE/'P06_EQUIPO_DSW-E06-recorrido.json').write_text(json.dumps(resultado, indent=2, ensure_ascii=False))
        assert no_auth[0] == 401 and invalido[0] == 400 and valido[0] == 201
        assert valido[1]['alerta'] == 'AFORO_ALTO' and historial[0] == 200 and int(sql) == 1

        frontend_log = open(tmp/'frontend.log', 'w')
        children.append(subprocess.Popen(['npm', 'start', '--', '--host', '127.0.0.1', '--port', '4200'],
            cwd=ROOT/'frontend-angular', stdout=frontend_log, stderr=subprocess.STDOUT))
        wait_url('http://127.0.0.1:4200/')
        run(['google-chrome', '--headless', '--no-sandbox', '--disable-gpu', '--hide-scrollbars',
             '--disable-dev-shm-usage', f'--user-data-dir={tmp}/chrome',
             '--window-size=1440,1000', '--virtual-time-budget=7000',
             f'--screenshot={EVIDENCE}/P06_EQUIPO_DSW-E06-dashboard.png',
             'http://127.0.0.1:4200/telemetria'], stdout=subprocess.DEVNULL)
        print('VERIFICADO: 401, 400, 201, GET, fila PostgreSQL, dashboard capturado')
    finally:
        for child in reversed(children):
            child.terminate()
            try: child.wait(timeout=8)
            except subprocess.TimeoutExpired: child.kill()
        if (tmp/'pg/postmaster.pid').exists():
            run([PG_BIN/'pg_ctl', '-D', tmp/'pg', 'stop', '-m', 'immediate'], stdout=subprocess.DEVNULL)
