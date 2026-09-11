-- PR06 - Control de asistencia con eventos
-- P02. Aplicación Web 1.0 con JSP, Tomcat y PostgreSQL
-- DSQ-E06

-- Tabla de sesiones
CREATE TABLE sesion (
    id_sesion SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20),
    descripcion VARCHAR(200)
);

-- Tabla de participantes
CREATE TABLE participante (
    id_participante SERIAL PRIMARY KEY,
    codigo_ficticio VARCHAR(30) UNIQUE NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    estado VARCHAR(20)
);

-- Tabla de registros de asistencia
CREATE TABLE registro (
    id_registro SERIAL PRIMARY KEY,
    id_sesion INT NOT NULL,
    id_participante INT NOT NULL,
    tipo_registro VARCHAR(20) NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(200),

    FOREIGN KEY (id_sesion)
        REFERENCES sesion(id_sesion),

    FOREIGN KEY (id_participante)
        REFERENCES participante(id_participante),

    UNIQUE (id_sesion, id_participante, tipo_registro)
);

-- Tabla de incidencias
CREATE TABLE incidencia (
    id_incidencia SERIAL PRIMARY KEY,
    id_registro INT NOT NULL,
    tipo_incidencia VARCHAR(30),
    descripcion VARCHAR(200),
    justificacion VARCHAR(200),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_registro)
        REFERENCES registro(id_registro)
);

-- Tabla de dispositivos
CREATE TABLE dispositivo (
    id_dispositivo SERIAL PRIMARY KEY,
    identificador_ficticio VARCHAR(30) UNIQUE NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    tipo VARCHAR(40),
    estado VARCHAR(20)
);

-- Tabla de eventos
CREATE TABLE evento (
    id_evento SERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL,
    id_sesion INT NOT NULL,
    id_participante INT,
    tipo_evento VARCHAR(30),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalle VARCHAR(200),

    FOREIGN KEY (id_dispositivo)
        REFERENCES dispositivo(id_dispositivo),

    FOREIGN KEY (id_sesion)
        REFERENCES sesion(id_sesion),

    FOREIGN KEY (id_participante)
        REFERENCES participante(id_participante)
);