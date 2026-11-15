-- P06: ejecutar una sola vez en la base PR06 ya inicializada.
CREATE TABLE IF NOT EXISTS lectura_iot (
    id_lectura BIGSERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL REFERENCES dispositivo(id_dispositivo),
    id_sesion INT NOT NULL REFERENCES sesion(id_sesion),
    fecha_hora TIMESTAMPTZ NOT NULL,
    variable VARCHAR(20) NOT NULL CHECK (variable = 'AFORO'),
    unidad VARCHAR(20) NOT NULL CHECK (unidad = 'personas'),
    valor NUMERIC(6,2) NOT NULL CHECK (valor >= 0 AND valor <= 100),
    origen VARCHAR(30) NOT NULL CHECK (origen = 'SIMULADOR_HTTP'),
    recibida_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT lectura_iot_unica UNIQUE (id_dispositivo, fecha_hora)
);
CREATE INDEX IF NOT EXISTS lectura_iot_sesion_fecha_idx ON lectura_iot (id_sesion, fecha_hora DESC);
