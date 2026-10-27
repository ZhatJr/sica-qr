-- =====================================================
-- SICA-QR · Datos de prueba
-- Motor: PostgreSQL 14 o superior
-- Requiere: 01_SICA_QR_estructura.sql
--
-- Datos ficticios para las 17 tablas. Ningún nombre ni
-- DNI corresponde a una persona real.
--
-- Usuarios (usuario / contraseña / rol):
--   admin      / admin123      / Administrador
--   auxiliar   / auxiliar123   / Auxiliar de educación
--   docente    / docente123    / Docente o tutor
--   direccion  / direccion123  / Dirección
--
-- Contraseñas almacenadas como SHA-256 de
-- "SICA-QR-14762" + contraseña (ver UtilClave.java).
-- =====================================================

BEGIN;

-- =====================================================
-- 1. ROLES Y USUARIOS
-- =====================================================

INSERT INTO rol (nombre, descripcion) VALUES
('ADMINISTRADOR', 'Mantiene cuentas, datos académicos, reglas de horario y credenciales'),
('AUXILIAR',      'Registra el ingreso y resuelve la contingencia por falta de credencial'),
('DOCENTE',       'Consulta la asistencia de sus secciones y tramita justificaciones'),
('DIRECCION',     'Supervisa indicadores, evalúa alertas y resuelve justificaciones');

INSERT INTO usuario (id_rol, nombre_usuario, clave_hash, nombres, apellidos, correo) VALUES
((SELECT id_rol FROM rol WHERE nombre = 'ADMINISTRADOR'), 'admin',
 '374ca1c6dcf652ef60b85fd5266c6a578c471362b27e8f2b978f73c786c1587d',
 'Rosa Elena', 'Purizaca Vite', 'admin@sicaqr.edu.pe'),
((SELECT id_rol FROM rol WHERE nombre = 'AUXILIAR'), 'auxiliar',
 '08353e7aaf6fd5d19b4e36bfae716aa199155a79f3913c4661eb00f30fd301cf',
 'Jorge Luis', 'Sernaqué Ramos', 'auxiliar@sicaqr.edu.pe'),
((SELECT id_rol FROM rol WHERE nombre = 'DOCENTE'), 'docente',
 '56bb6f14bfaaf3f241c3f91067e40c59e6eead134f90a6bbf7f659b518302e74',
 'Carmen Rosa', 'Yarlequé Ñunura', 'docente@sicaqr.edu.pe'),
((SELECT id_rol FROM rol WHERE nombre = 'DIRECCION'), 'direccion',
 '55a53da742eb9c798bcd0f9b195d542fea5e98dae0d156ef745e71a9ec8844e5',
 'Manuel Antonio', 'Chunga Flores', 'direccion@sicaqr.edu.pe');

-- =====================================================
-- 2. ORGANIZACIÓN ACADÉMICA
-- =====================================================

INSERT INTO grado (nombre, nivel_educativo) VALUES
('5 años', 'INICIAL'),
('1.°', 'PRIMARIA'), ('2.°', 'PRIMARIA'), ('3.°', 'PRIMARIA'),
('4.°', 'PRIMARIA'), ('5.°', 'PRIMARIA'), ('6.°', 'PRIMARIA'),
('1.°', 'SECUNDARIA'), ('2.°', 'SECUNDARIA'), ('3.°', 'SECUNDARIA'),
('4.°', 'SECUNDARIA'), ('5.°', 'SECUNDARIA');

-- Secciones A y B para todos los grados
INSERT INTO seccion (id_grado, nombre)
SELECT g.id_grado, s.nombre
FROM grado g
CROSS JOIN (VALUES ('A'), ('B')) AS s(nombre)
ORDER BY g.id_grado, s.nombre;

INSERT INTO periodo_academico (anio, fecha_inicio, fecha_fin, estado)
VALUES (2026, DATE '2026-03-09', DATE '2026-12-18', 'ACTIVO');

-- =====================================================
-- 3. CONFIGURACIÓN INSTITUCIONAL (RF-02)
--    Entrada 07:30, tolerancia 10 min, corte 09:00
--    -> hasta 07:40 PUNTUAL, hasta 08:59 TARDANZA
-- =====================================================

INSERT INTO configuracion (
    id_periodo, id_usuario_creador, hora_entrada, tolerancia_minutos,
    hora_corte, plazo_justificacion_dias, umbral_tardanzas,
    umbral_inasistencias, vigente_desde
) VALUES (
    (SELECT id_periodo FROM periodo_academico WHERE anio = 2026),
    (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'),
    TIME '07:30', 10, TIME '09:00', 3, 3, 3, DATE '2026-03-09'
);

-- =====================================================
-- 4. CALENDARIO ESCOLAR
--    Lunes a viernes laborables, salvo feriados
-- =====================================================

INSERT INTO calendario_escolar (id_periodo, fecha, es_laborable, descripcion)
SELECT
    p.id_periodo,
    d::DATE,
    CASE
        WHEN EXTRACT(ISODOW FROM d) IN (6, 7) THEN FALSE
        WHEN f.fecha IS NOT NULL THEN FALSE
        ELSE TRUE
    END,
    CASE
        WHEN f.fecha IS NOT NULL THEN f.motivo
        WHEN EXTRACT(ISODOW FROM d) IN (6, 7) THEN 'Fin de semana'
        ELSE 'Día de clases'
    END
FROM periodo_academico p
CROSS JOIN generate_series(p.fecha_inicio, p.fecha_fin, INTERVAL '1 day') AS d
LEFT JOIN (VALUES
    (DATE '2026-04-02', 'Jueves Santo'),
    (DATE '2026-04-03', 'Viernes Santo'),
    (DATE '2026-05-01', 'Día del Trabajo'),
    (DATE '2026-06-29', 'San Pedro y San Pablo'),
    (DATE '2026-07-23', 'Día de la Fuerza Aérea'),
    (DATE '2026-07-28', 'Fiestas Patrias'),
    (DATE '2026-07-29', 'Fiestas Patrias'),
    (DATE '2026-08-06', 'Batalla de Junín'),
    (DATE '2026-10-08', 'Combate de Angamos'),
    (DATE '2026-12-08', 'Inmaculada Concepción')
) AS f(fecha, motivo) ON f.fecha = d::DATE
WHERE p.anio = 2026;

-- =====================================================
-- 5. ESTUDIANTES
-- =====================================================

INSERT INTO estudiante (codigo, dni, nombres, apellidos, fecha_nacimiento) VALUES
('EST-2026-0001', '81234501', 'Luciana Valeria', 'Alvarado Sosa',     DATE '2015-04-12'),
('EST-2026-0002', '81234502', 'Mateo André',     'Benites Panta',     DATE '2015-08-03'),
('EST-2026-0003', '81234503', 'Camila Fernanda', 'Castillo Ruiz',     DATE '2015-01-27'),
('EST-2026-0004', '81234504', 'Thiago Alonso',   'Eche Zapata',       DATE '2015-11-15'),
('EST-2026-0005', '81234505', 'Valentina Sofía', 'García Temoche',    DATE '2015-06-09'),
('EST-2026-0006', '81234506', 'Sebastián José',  'Huamán Ipanaqué',   DATE '2014-03-21'),
('EST-2026-0007', '81234507', 'Ariana Nicole',   'Juárez Morales',    DATE '2014-09-30'),
('EST-2026-0008', '81234508', 'Diego Alejandro', 'López Chiroque',    DATE '2014-12-05'),
('EST-2026-0009', '81234509', 'Fernanda Lucía',  'Mendoza Querevalú', DATE '2014-07-18'),
('EST-2026-0010', '81234510', 'Joaquín Elías',   'Nole Silupú',       DATE '2014-02-11'),
('EST-2026-0011', '81234511', 'Ximena Paola',    'Ordinola Vilela',   DATE '2011-05-24'),
('EST-2026-0012', '81234512', 'Adrián Fabricio', 'Paico Sandoval',    DATE '2011-10-02'),
('EST-2026-0013', '81234513', 'Antonella Belén', 'Querevalú Ramos',   DATE '2011-08-14'),
('EST-2026-0014', '81234514', 'Gael Matías',     'Ramírez Saavedra',  DATE '2011-01-07'),
('EST-2026-0015', '81234515', 'Mía Alessandra',  'Sernaqué Talledo',  DATE '2011-12-19'),
('EST-2026-0016', '81234516', 'Bruno Sebastián', 'Talledo Urbina',    DATE '2009-06-26'),
('EST-2026-0017', '81234517', 'Danna Isabel',    'Urbina Vite',       DATE '2009-03-13'),
('EST-2026-0018', '81234518', 'Leonardo Daniel', 'Vílchez Yovera',    DATE '2009-09-08'),
('EST-2026-0019', '81234519', 'Renata Milagros', 'Yovera Zeta',       DATE '2009-04-29'),
('EST-2026-0020', '81234520', 'Ian Gabriel',     'Zapata Alvarado',   DATE '2009-11-22');

-- =====================================================
-- 6. APODERADOS Y VÍNCULO CON ESTUDIANTES
-- =====================================================

INSERT INTO apoderado (dni, nombres, apellidos, telefono, correo, canal_preferido) VALUES
('41234501', 'María Elena',    'Sosa Paiva',        '987654301', 'msosa@correo.pe',     'WHATSAPP'),
('41234502', 'Pedro Pablo',    'Benites Lalupu',    '987654302', NULL,                  'WHATSAPP'),
('41234503', 'Gloria Isabel',  'Ruiz Cordova',      '987654303', 'gruiz@correo.pe',     'CORREO'),
('41234504', 'Luis Alberto',   'Eche Jacinto',      '987654304', NULL,                  'SMS'),
('41234505', 'Rosa María',     'Temoche Inga',      '987654305', NULL,                  'WHATSAPP'),
('41234506', 'Juan Carlos',    'Huamán Puescas',    '987654306', 'jhuaman@correo.pe',   'WHATSAPP'),
('41234507', 'Patricia',       'Morales Ancajima',  '987654307', NULL,                  'LLAMADA'),
('41234508', 'Ricardo',        'López Fiestas',     '987654308', NULL,                  'WHATSAPP'),
('41234509', 'Silvia Roxana',  'Querevalú Nima',    '987654309', 'squerevalu@correo.pe','CORREO'),
('41234510', 'Miguel Ángel',   'Nole Carrasco',     '987654310', NULL,                  'WHATSAPP'),
('41234511', 'Ana Lucía',      'Vilela Ojeda',      '987654311', NULL,                  'WHATSAPP'),
('41234512', 'Víctor Hugo',    'Paico More',        '987654312', NULL,                  'SMS'),
('41234513', 'Elena',          'Ramos Pazo',        '987654313', 'eramos@correo.pe',    'WHATSAPP'),
('41234514', 'José Manuel',    'Ramírez Ato',       '987654314', NULL,                  'WHATSAPP'),
('41234515', 'Carla Patricia', 'Talledo Sullon',    '987654315', NULL,                  'WHATSAPP'),
('41234516', 'Héctor',         'Talledo Ayala',     '987654316', NULL,                  'LLAMADA'),
('41234517', 'Milagros',       'Vite Correa',       '987654317', 'mvite@correo.pe',     'CORREO'),
('41234518', 'Raúl Eduardo',   'Vílchez Pinday',    '987654318', NULL,                  'WHATSAPP'),
('41234519', 'Teresa',         'Zeta Mogollon',     '987654319', NULL,                  'WHATSAPP');

-- Apoderado principal de cada estudiante (1 a 19)
INSERT INTO estudiante_apoderado (id_estudiante, id_apoderado, parentesco, es_principal)
SELECT e.id_estudiante, a.id_apoderado,
       CASE WHEN e.id_estudiante % 3 = 0 THEN 'Padre' ELSE 'Madre' END,
       TRUE
FROM estudiante e
JOIN apoderado a ON a.dni = '412345' || LPAD(e.id_estudiante::TEXT, 2, '0')
WHERE e.id_estudiante <= 19;

-- Apoderado compartido por los estudiantes 1 y 20
INSERT INTO estudiante_apoderado (id_estudiante, id_apoderado, parentesco, es_principal)
VALUES (20, 1, 'Madre', TRUE);

-- Un estudiante con segundo apoderado, no principal
INSERT INTO estudiante_apoderado (id_estudiante, id_apoderado, parentesco, es_principal, recibe_notificaciones)
VALUES (15, 16, 'Tío', FALSE, FALSE);

-- =====================================================
-- 7. MATRÍCULAS
--    1-5   -> Primaria 5.° A
--    6-10  -> Primaria 6.° A
--    11-15 -> Secundaria 3.° B
--    16-20 -> Secundaria 5.° B
-- =====================================================

INSERT INTO matricula (id_estudiante, id_seccion, id_periodo, fecha_matricula)
SELECT e.id_estudiante,
       s.id_seccion,
       (SELECT id_periodo FROM periodo_academico WHERE anio = 2026),
       DATE '2026-02-20'
FROM estudiante e
JOIN seccion s ON s.id_seccion = (
    SELECT s2.id_seccion
    FROM seccion s2
    JOIN grado g ON g.id_grado = s2.id_grado
    WHERE (e.id_estudiante BETWEEN 1  AND 5  AND g.nombre = '5.°' AND g.nivel_educativo = 'PRIMARIA'   AND s2.nombre = 'A')
       OR (e.id_estudiante BETWEEN 6  AND 10 AND g.nombre = '6.°' AND g.nivel_educativo = 'PRIMARIA'   AND s2.nombre = 'A')
       OR (e.id_estudiante BETWEEN 11 AND 15 AND g.nombre = '3.°' AND g.nivel_educativo = 'SECUNDARIA' AND s2.nombre = 'B')
       OR (e.id_estudiante BETWEEN 16 AND 20 AND g.nombre = '5.°' AND g.nivel_educativo = 'SECUNDARIA' AND s2.nombre = 'B')
);

-- =====================================================
-- 8. CREDENCIALES QR (RF-11)
--    El token es un UUID aleatorio: no contiene datos
--    del estudiante (Ley N.° 29733)
-- =====================================================

-- Credencial revocada del estudiante 4 (perdida)
INSERT INTO credencial (id_estudiante, id_usuario_emisor, id_usuario_revocador,
                        estado, emitida_en, revocada_en, motivo_revocacion)
VALUES (4,
        (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'),
        (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'),
        'REVOCADA',
        TIMESTAMPTZ '2026-03-05 10:00:00-05',
        TIMESTAMPTZ '2026-05-12 08:15:00-05',
        'Pérdida reportada por el apoderado');

-- Credencial activa para los 20 estudiantes
INSERT INTO credencial (id_estudiante, id_usuario_emisor, emitida_en)
SELECT e.id_estudiante,
       (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'),
       CASE WHEN e.id_estudiante = 4
            THEN TIMESTAMPTZ '2026-05-12 08:20:00-05'
            ELSE TIMESTAMPTZ '2026-03-05 10:00:00-05' END
FROM estudiante e;

-- =====================================================
-- 9. REGISTROS DE ASISTENCIA (RF-01, RF-02, RF-10)
--    Últimos 10 días laborables anteriores a la fecha actual.
--    Puntual <= 07:40 < Tardanza < 09:00 <= Inasistencia
-- =====================================================

WITH dias AS (
    SELECT fecha
    FROM calendario_escolar
    WHERE es_laborable AND fecha < CURRENT_DATE
    ORDER BY fecha DESC
    LIMIT 10
),
base AS (
    SELECT m.id_matricula,
           d.fecha,
           ((m.id_matricula * 37 + EXTRACT(DOY FROM d.fecha)::INT * 11) % 100) AS r
    FROM matricula m
    CROSS JOIN dias d
),
horas AS (
    SELECT id_matricula, fecha, r,
           CASE
               WHEN r < 8  THEN TIME '09:00'
               WHEN r < 25 THEN TIME '07:41' + (r % 30) * INTERVAL '1 minute'
               ELSE             TIME '07:00' + (r % 40) * INTERVAL '1 minute'
           END AS hora,
           CASE
               WHEN r < 8  THEN 'INASISTENCIA'
               WHEN r < 25 THEN 'TARDANZA'
               ELSE             'PUNTUAL'
           END AS estado,
           CASE
               WHEN r < 8       THEN 'AUTOMATICO'
               WHEN r % 13 = 0  THEN 'MANUAL'
               ELSE                  'QR'
           END AS metodo
    FROM base
)
INSERT INTO registro_asistencia (
    id_matricula, id_usuario_registro, fecha_asistencia,
    capturado_en, registrado_en, estado, metodo_registro,
    origen_registro, estado_sincronizacion, sincronizado_en, observacion
)
SELECT
    h.id_matricula,
    (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'auxiliar'),
    h.fecha,
    (h.fecha + h.hora) AT TIME ZONE 'America/Lima',
    -- Algunos registros se leyeron sin conexion y se
    -- sincronizaron mas tarde: registrado_en > capturado_en
    CASE WHEN h.r % 11 = 0 AND h.estado <> 'INASISTENCIA'
         THEN (h.fecha + TIME '09:30') AT TIME ZONE 'America/Lima'
         ELSE (h.fecha + h.hora) AT TIME ZONE 'America/Lima' + INTERVAL '2 seconds' END,
    h.estado,
    h.metodo,
    CASE WHEN h.r % 11 = 0 AND h.estado <> 'INASISTENCIA'
         THEN 'SIN_CONEXION' ELSE 'EN_LINEA' END,
    'SINCRONIZADO',
    CASE WHEN h.r % 11 = 0 AND h.estado <> 'INASISTENCIA'
         THEN (h.fecha + TIME '09:30') AT TIME ZONE 'America/Lima' END,
    CASE WHEN h.metodo = 'MANUAL'
         THEN 'Registro por DNI: el estudiante no portó su credencial' END
FROM horas h;

-- =====================================================
-- 10. JUSTIFICACIONES (RF-05, RF-06)
-- =====================================================

-- Justificaciones sobre inasistencias: una aprobada y dos pendientes
INSERT INTO justificacion (
    id_registro, id_usuario_registro, id_usuario_revisor, motivo,
    observacion, estado, presentada_en, revisada_en, comentario_revision
)
SELECT ra.id_registro,
       (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'docente'),
       CASE WHEN fila = 1 THEN (SELECT id_usuario FROM usuario WHERE nombre_usuario = 'direccion') END,
       CASE WHEN fila % 2 = 1 THEN 'Salud' ELSE 'Motivo familiar' END,
       'Presentado por el apoderado en la agenda escolar',
       CASE WHEN fila = 1 THEN 'APROBADA' ELSE 'PENDIENTE' END,
       ra.capturado_en + INTERVAL '1 day',
       CASE WHEN fila = 1 THEN ra.capturado_en + INTERVAL '2 days' END,
       CASE WHEN fila = 1 THEN 'Sustento conforme' END
FROM (
    SELECT id_registro, capturado_en,
           ROW_NUMBER() OVER (ORDER BY fecha_asistencia, id_matricula) AS fila
    FROM registro_asistencia
    WHERE estado = 'INASISTENCIA'
) ra
WHERE fila <= 3;

-- =====================================================
-- 11. ALERTAS POR ACUMULACIÓN (RF-08)
--     Matrículas que superan el umbral de 3 tardanzas
-- =====================================================

INSERT INTO alerta (id_matricula, tipo, nivel, cantidad_acumulada, estado)
SELECT id_matricula,
       'TARDANZAS',
       CASE WHEN COUNT(*) >= 5 THEN 'CRITICA' ELSE 'PREVENTIVA' END,
       COUNT(*),
       'PENDIENTE'
FROM registro_asistencia
WHERE estado = 'TARDANZA'
GROUP BY id_matricula
HAVING COUNT(*) >= 3;

-- =====================================================
-- 12. NOTIFICACIONES AL APODERADO (RF-09)
--     Una por cada tardanza o inasistencia, dirigida al
--     apoderado principal
-- =====================================================

INSERT INTO notificacion (
    id_registro, id_apoderado, canal, destino, mensaje,
    estado, numero_intentos, creada_en, enviada_en, detalle_error
)
SELECT ra.id_registro,
       a.id_apoderado,
       CASE WHEN a.canal_preferido = 'LLAMADA' THEN 'SMS' ELSE a.canal_preferido END,
       CASE WHEN a.canal_preferido = 'CORREO' THEN a.correo ELSE a.telefono END,
       'I.E. 14762: ' || e.nombres || ' registró ' || LOWER(ra.estado)
           || ' el ' || TO_CHAR(ra.fecha_asistencia, 'DD/MM/YYYY') || '.',
       CASE WHEN ra.id_registro % 10 = 0 THEN 'FALLIDA'
            WHEN ra.fecha_asistencia = (SELECT MAX(fecha_asistencia) FROM registro_asistencia)
                 AND ra.id_registro % 3 = 0 THEN 'PENDIENTE'
            ELSE 'ENVIADA' END,
       CASE WHEN ra.id_registro % 10 = 0 THEN 3 ELSE 1 END,
       ra.registrado_en,
       CASE WHEN ra.id_registro % 10 = 0 THEN NULL
            WHEN ra.fecha_asistencia = (SELECT MAX(fecha_asistencia) FROM registro_asistencia)
                 AND ra.id_registro % 3 = 0 THEN NULL
            ELSE ra.registrado_en + INTERVAL '1 minute' END,
       CASE WHEN ra.id_registro % 10 = 0 THEN 'Número no disponible en el proveedor' END
FROM registro_asistencia ra
JOIN matricula m             ON m.id_matricula = ra.id_matricula
JOIN estudiante e            ON e.id_estudiante = m.id_estudiante
JOIN estudiante_apoderado ea ON ea.id_estudiante = e.id_estudiante
                            AND ea.es_principal AND ea.recibe_notificaciones AND ea.activo
JOIN apoderado a             ON a.id_apoderado = ea.id_apoderado
WHERE ra.estado IN ('TARDANZA', 'INASISTENCIA');

-- =====================================================
-- 13. BITÁCORA DE AUDITORÍA (RF-12)
-- =====================================================

INSERT INTO auditoria (id_usuario, tipo_operacion, tabla_afectada, id_registro_afectado, detalle, direccion_ip, realizado_en)
VALUES
((SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'), 'LOGIN', 'usuario', NULL,
 '{"resultado": "exitoso"}', '192.168.1.10', TIMESTAMPTZ '2026-03-05 09:55:00-05'),
((SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'), 'INSERT', 'credencial', NULL,
 '{"accion": "emisión masiva", "cantidad": 20}', '192.168.1.10', TIMESTAMPTZ '2026-03-05 10:00:00-05'),
((SELECT id_usuario FROM usuario WHERE nombre_usuario = 'admin'), 'UPDATE', 'credencial', 1,
 '{"estado_anterior": "ACTIVA", "estado_nuevo": "REVOCADA", "motivo": "Pérdida reportada por el apoderado"}',
 '192.168.1.10', TIMESTAMPTZ '2026-05-12 08:15:00-05'),
((SELECT id_usuario FROM usuario WHERE nombre_usuario = 'auxiliar'), 'LOGIN', 'usuario', NULL,
 '{"resultado": "exitoso"}', '192.168.1.21', CURRENT_TIMESTAMP - INTERVAL '1 day'),
((SELECT id_usuario FROM usuario WHERE nombre_usuario = 'direccion'), 'EXPORT', 'registro_asistencia', NULL,
 '{"reporte": "R02", "formato": "XLSX"}', '192.168.1.30', CURRENT_TIMESTAMP - INTERVAL '1 day');

COMMIT;

-- =====================================================
-- COMPROBACIÓN: cantidad de filas por tabla
-- =====================================================

SELECT 'rol' AS tabla, COUNT(*) AS filas FROM rol
UNION ALL SELECT 'usuario',              COUNT(*) FROM usuario
UNION ALL SELECT 'auditoria',            COUNT(*) FROM auditoria
UNION ALL SELECT 'grado',                COUNT(*) FROM grado
UNION ALL SELECT 'seccion',              COUNT(*) FROM seccion
UNION ALL SELECT 'periodo_academico',    COUNT(*) FROM periodo_academico
UNION ALL SELECT 'estudiante',           COUNT(*) FROM estudiante
UNION ALL SELECT 'apoderado',            COUNT(*) FROM apoderado
UNION ALL SELECT 'estudiante_apoderado', COUNT(*) FROM estudiante_apoderado
UNION ALL SELECT 'matricula',            COUNT(*) FROM matricula
UNION ALL SELECT 'configuracion',        COUNT(*) FROM configuracion
UNION ALL SELECT 'calendario_escolar',   COUNT(*) FROM calendario_escolar
UNION ALL SELECT 'credencial',           COUNT(*) FROM credencial
UNION ALL SELECT 'registro_asistencia',  COUNT(*) FROM registro_asistencia
UNION ALL SELECT 'justificacion',        COUNT(*) FROM justificacion
UNION ALL SELECT 'alerta',               COUNT(*) FROM alerta
UNION ALL SELECT 'notificacion',         COUNT(*) FROM notificacion;
