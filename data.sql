-- PR06 - Datos de prueba
-- P02. Aplicación Web 1.0 con JSP, Tomcat y PostgreSQL
-- DSQ-E06

-- Sesiones
INSERT INTO sesion
(nombre, fecha, hora_inicio, hora_fin, estado, descripcion)
VALUES
('Sesion de bienvenida', '2026-09-15', '09:00', '11:00', 'PROGRAMADA',
 'Sesion de prueba para registro de asistencia'),

('Taller de desarrollo web', '2026-09-16', '10:00', '12:00', 'PROGRAMADA',
 'Taller con participantes ficticios');


-- Participantes ficticios
INSERT INTO participante
(codigo_ficticio, nombre, apellido, estado)
VALUES
('P001', 'Ana', 'Lopez', 'ACTIVO'),

('P002', 'Carlos', 'Martinez', 'ACTIVO'),

('P003', 'Laura', 'Hernandez', 'ACTIVO'),

('P004', 'Miguel', 'Torres', 'ACTIVO');


-- Dispositivo ficticio
INSERT INTO dispositivo
(identificador_ficticio, nombre, tipo, estado)
VALUES
('D001', 'Dispositivo de prueba', 'LECTOR', 'ACTIVO');