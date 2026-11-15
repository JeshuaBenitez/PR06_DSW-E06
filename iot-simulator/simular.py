#!/usr/bin/env python3
"""Fuente de aforo ficticio para una sesión. Solo usa la API local."""
import argparse
import base64
import datetime as dt
import json
import os
import time
import urllib.error
import urllib.request

parser = argparse.ArgumentParser()
parser.add_argument('--sesion', type=int, default=1)
parser.add_argument('--dispositivo', type=int, default=1)
parser.add_argument('--valor', type=int, help='Envía un valor fijo para reproducir una alerta')
parser.add_argument('--intervalo', type=int, default=5)
parser.add_argument('--una-vez', action='store_true')
args = parser.parse_args()
if args.sesion < 1 or args.dispositivo < 1 or args.intervalo < 1:
    parser.error('IDs e intervalo deben ser positivos')

url = os.getenv('PR06_API_URL', 'http://127.0.0.1:8080/api/telemetria')
if not url.startswith('http://127.0.0.1:') and not url.startswith('http://localhost:'):
    parser.error('La simulación se limita al servicio local')
usuario = os.getenv('API_BASIC_USER')
clave = os.getenv('API_BASIC_PASSWORD')
if not usuario or not clave:
    parser.error('Configura API_BASIC_USER y API_BASIC_PASSWORD en el entorno')
auth = base64.b64encode(f'{usuario}:{clave}'.encode()).decode()
contador = 0
while True:
    valor = args.valor if args.valor is not None else [12, 15, 18, 21, 26, 19][contador % 6]
    dato = {
        'idDispositivo': args.dispositivo, 'idSesion': args.sesion,
        'fechaHora': dt.datetime.now(dt.timezone.utc).isoformat(),
        'variable': 'AFORO', 'unidad': 'personas', 'valor': valor,
        'origen': 'SIMULADOR_HTTP'
    }
    req = urllib.request.Request(url, json.dumps(dato).encode(),
        headers={'Content-Type': 'application/json', 'Authorization': f'Basic {auth}'}, method='POST')
    try:
        with urllib.request.urlopen(req, timeout=8) as respuesta:
            item = json.load(respuesta)
            print(f"{item['fechaHora']} lectura={item['idLectura']} valor={item['valor']} alerta={item['alerta']}", flush=True)
    except urllib.error.HTTPError as error:
        print(f'HTTP {error.code}: la API rechazó la lectura', flush=True)
    except urllib.error.URLError:
        print('Sin conexión con la API local', flush=True)
    contador += 1
    if args.una_vez:
        break
    time.sleep(args.intervalo)
