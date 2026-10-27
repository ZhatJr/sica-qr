-- =====================================================
-- SICA-QR · Script de construccion de la base de datos
-- Motor: PostgreSQL 14 o superior
-- 17 tablas · Segundo avance del proyecto final
--
-- Correcciones aplicadas sobre el modelo preliminar:
--   1. Se separa capturado_en (hora real de lectura en el
--      dispositivo) de registrado_en (hora de insercion en
--      el servidor). La clasificacion usa capturado_en.
--   2. La unicidad de justificacion pasa a indice parcial,
--      de modo que una solicitud rechazada pueda volver a
--      presentarse.
--   3. Se agrega indice por fecha y hora de captura para
--      los reportes diarios.
-- =====================================================

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================
-- SEGURIDAD Y CONTROL DE ACCESO
-- =====================================================

CREATE TABLE rol (
    id_rol BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(30) NOT NULL,
    descripcion VARCHAR(150),
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

CREATE TABLE usuario (
    id_usuario BIGINT GENERATED ALWAYS AS IDENTITY,
    id_rol BIGINT NOT NULL,
    nombre_usuario VARCHAR(50) NOT NULL,
    clave_hash VARCHAR(255) NOT NULL,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    intentos_fallidos SMALLINT NOT NULL DEFAULT 0,
    ultimo_acceso TIMESTAMPTZ,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_nombre UNIQUE (nombre_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT ck_usuario_intentos
        CHECK (intentos_fallidos >= 0),
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
        ON DELETE RESTRICT
);

CREATE TABLE auditoria (
    id_auditoria BIGINT GENERATED ALWAYS AS IDENTITY,
    id_usuario BIGINT NOT NULL,
    tipo_operacion VARCHAR(20) NOT NULL,
    tabla_afectada VARCHAR(60) NOT NULL,
    id_registro_afectado BIGINT,
    detalle JSONB,
    direccion_ip INET,
    realizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_auditoria PRIMARY KEY (id_auditoria),
    CONSTRAINT ck_auditoria_operacion
        CHECK (
            tipo_operacion IN (
                'INSERT', 'UPDATE', 'DELETE',
                'LOGIN', 'LOGOUT', 'EXPORT'
            )
        ),
    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

-- =====================================================
-- ORGANIZACIÓN ACADÉMICA
-- =====================================================

CREATE TABLE grado (
    id_grado BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(30) NOT NULL,
    nivel_educativo VARCHAR(15) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_grado PRIMARY KEY (id_grado),
    CONSTRAINT uq_grado_nombre_nivel
        UNIQUE (nombre, nivel_educativo),
    CONSTRAINT ck_grado_nivel
        CHECK (
            nivel_educativo IN (
                'INICIAL', 'PRIMARIA', 'SECUNDARIA'
            )
        )
);

CREATE TABLE seccion (
    id_seccion BIGINT GENERATED ALWAYS AS IDENTITY,
    id_grado BIGINT NOT NULL,
    nombre VARCHAR(10) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_seccion PRIMARY KEY (id_seccion),
    CONSTRAINT uq_seccion_grado_nombre
        UNIQUE (id_grado, nombre),
    CONSTRAINT fk_seccion_grado
        FOREIGN KEY (id_grado)
        REFERENCES grado(id_grado)
        ON DELETE RESTRICT
);

CREATE TABLE periodo_academico (
    id_periodo BIGINT GENERATED ALWAYS AS IDENTITY,
    anio SMALLINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'PLANIFICADO',

    CONSTRAINT pk_periodo_academico PRIMARY KEY (id_periodo),
    CONSTRAINT uq_periodo_anio UNIQUE (anio),
    CONSTRAINT ck_periodo_anio
        CHECK (anio BETWEEN 2000 AND 2100),
    CONSTRAINT ck_periodo_fechas
        CHECK (fecha_fin > fecha_inicio),
    CONSTRAINT ck_periodo_estado
        CHECK (
            estado IN ('PLANIFICADO', 'ACTIVO', 'CERRADO')
        )
);

-- =====================================================
-- ESTUDIANTES Y APODERADOS
-- =====================================================

CREATE TABLE estudiante (
    id_estudiante BIGINT GENERATED ALWAYS AS IDENTITY,
    codigo VARCHAR(20) NOT NULL,
    dni CHAR(8) NOT NULL,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    foto_url VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_estudiante PRIMARY KEY (id_estudiante),
    CONSTRAINT uq_estudiante_codigo UNIQUE (codigo),
    CONSTRAINT uq_estudiante_dni UNIQUE (dni),
    CONSTRAINT ck_estudiante_dni
        CHECK (dni ~ '^[0-9]{8}$')
);

CREATE TABLE apoderado (
    id_apoderado BIGINT GENERATED ALWAYS AS IDENTITY,
    dni CHAR(8) NOT NULL,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(254),
    canal_preferido VARCHAR(15) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_apoderado PRIMARY KEY (id_apoderado),
    CONSTRAINT uq_apoderado_dni UNIQUE (dni),
    CONSTRAINT ck_apoderado_dni
        CHECK (dni ~ '^[0-9]{8}$'),
    CONSTRAINT ck_apoderado_canal
        CHECK (
            canal_preferido IN (
                'WHATSAPP', 'CORREO', 'SMS', 'LLAMADA'
            )
        )
);

CREATE TABLE estudiante_apoderado (
    id_estudiante_apoderado BIGINT GENERATED ALWAYS AS IDENTITY,
    id_estudiante BIGINT NOT NULL,
    id_apoderado BIGINT NOT NULL,
    parentesco VARCHAR(30) NOT NULL,
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,
    recibe_notificaciones BOOLEAN NOT NULL DEFAULT TRUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_estudiante_apoderado
        PRIMARY KEY (id_estudiante_apoderado),
    CONSTRAINT uq_estudiante_apoderado
        UNIQUE (id_estudiante, id_apoderado),
    CONSTRAINT fk_estudiante_apoderado_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiante(id_estudiante)
        ON DELETE RESTRICT,
    CONSTRAINT fk_estudiante_apoderado_apoderado
        FOREIGN KEY (id_apoderado)
        REFERENCES apoderado(id_apoderado)
        ON DELETE RESTRICT
);

CREATE TABLE matricula (
    id_matricula BIGINT GENERATED ALWAYS AS IDENTITY,
    id_estudiante BIGINT NOT NULL,
    id_seccion BIGINT NOT NULL,
    id_periodo BIGINT NOT NULL,
    fecha_matricula DATE NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVA',

    CONSTRAINT pk_matricula PRIMARY KEY (id_matricula),
    CONSTRAINT uq_matricula_estudiante_periodo
        UNIQUE (id_estudiante, id_periodo),
    CONSTRAINT ck_matricula_estado
        CHECK (
            estado IN (
                'ACTIVA', 'RETIRADA',
                'TRASLADADA', 'FINALIZADA'
            )
        ),
    CONSTRAINT fk_matricula_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiante(id_estudiante)
        ON DELETE RESTRICT,
    CONSTRAINT fk_matricula_seccion
        FOREIGN KEY (id_seccion)
        REFERENCES seccion(id_seccion)
        ON DELETE RESTRICT,
    CONSTRAINT fk_matricula_periodo
        FOREIGN KEY (id_periodo)
        REFERENCES periodo_academico(id_periodo)
        ON DELETE RESTRICT
);

-- =====================================================
-- CONFIGURACIÓN INSTITUCIONAL
-- =====================================================

CREATE TABLE configuracion (
    id_configuracion BIGINT GENERATED ALWAYS AS IDENTITY,
    id_periodo BIGINT NOT NULL,
    id_usuario_creador BIGINT NOT NULL,
    hora_entrada TIME NOT NULL,
    tolerancia_minutos SMALLINT NOT NULL,
    hora_corte TIME NOT NULL,
    plazo_justificacion_dias SMALLINT NOT NULL,
    umbral_tardanzas SMALLINT NOT NULL,
    umbral_inasistencias SMALLINT NOT NULL,
    vigente_desde DATE NOT NULL,
    vigente_hasta DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_configuracion PRIMARY KEY (id_configuracion),
    CONSTRAINT ck_configuracion_tolerancia
        CHECK (tolerancia_minutos BETWEEN 0 AND 120),
    CONSTRAINT ck_configuracion_plazo
        CHECK (plazo_justificacion_dias BETWEEN 0 AND 30),
    CONSTRAINT ck_configuracion_umbral_tardanzas
        CHECK (umbral_tardanzas > 0),
    CONSTRAINT ck_configuracion_umbral_inasistencias
        CHECK (umbral_inasistencias > 0),
    CONSTRAINT ck_configuracion_horas
        CHECK (hora_corte > hora_entrada),
    CONSTRAINT ck_configuracion_vigencia
        CHECK (
            vigente_hasta IS NULL
            OR vigente_hasta >= vigente_desde
        ),
    CONSTRAINT fk_configuracion_periodo
        FOREIGN KEY (id_periodo)
        REFERENCES periodo_academico(id_periodo)
        ON DELETE RESTRICT,
    CONSTRAINT fk_configuracion_usuario
        FOREIGN KEY (id_usuario_creador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

CREATE TABLE calendario_escolar (
    id_calendario BIGINT GENERATED ALWAYS AS IDENTITY,
    id_periodo BIGINT NOT NULL,
    fecha DATE NOT NULL,
    es_laborable BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion VARCHAR(150),

    CONSTRAINT pk_calendario_escolar PRIMARY KEY (id_calendario),
    CONSTRAINT uq_calendario_periodo_fecha
        UNIQUE (id_periodo, fecha),
    CONSTRAINT fk_calendario_periodo
        FOREIGN KEY (id_periodo)
        REFERENCES periodo_academico(id_periodo)
        ON DELETE RESTRICT
);

-- =====================================================
-- CREDENCIALES Y ASISTENCIA
-- =====================================================

CREATE TABLE credencial (
    id_credencial BIGINT GENERATED ALWAYS AS IDENTITY,
    id_estudiante BIGINT NOT NULL,
    id_usuario_emisor BIGINT NOT NULL,
    id_usuario_revocador BIGINT,
    token_uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVA',
    emitida_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vence_en TIMESTAMPTZ,
    revocada_en TIMESTAMPTZ,
    motivo_revocacion VARCHAR(200),

    CONSTRAINT pk_credencial PRIMARY KEY (id_credencial),
    CONSTRAINT uq_credencial_token UNIQUE (token_uuid),
    CONSTRAINT ck_credencial_estado
        CHECK (estado IN ('ACTIVA', 'REVOCADA', 'VENCIDA')),
    CONSTRAINT ck_credencial_vencimiento
        CHECK (
            vence_en IS NULL
            OR vence_en > emitida_en
        ),
    CONSTRAINT ck_credencial_revocacion
        CHECK (
            revocada_en IS NULL
            OR revocada_en >= emitida_en
        ),
    CONSTRAINT fk_credencial_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiante(id_estudiante)
        ON DELETE RESTRICT,
    CONSTRAINT fk_credencial_emisor
        FOREIGN KEY (id_usuario_emisor)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT,
    CONSTRAINT fk_credencial_revocador
        FOREIGN KEY (id_usuario_revocador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

CREATE TABLE registro_asistencia (
    id_registro BIGINT GENERATED ALWAYS AS IDENTITY,
    id_matricula BIGINT NOT NULL,
    id_usuario_registro BIGINT NOT NULL,
    fecha_asistencia DATE NOT NULL,
    -- hora real en que el dispositivo leyó la credencial:
    -- es la que determina el estado PUNTUAL o TARDANZA
    capturado_en TIMESTAMPTZ NOT NULL,
    -- hora en que el servidor insertó la fila; difiere de
    -- capturado_en cuando el registro se hizo sin conexión
    registrado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(15) NOT NULL,
    metodo_registro VARCHAR(15) NOT NULL,
    uuid_sincronizacion UUID NOT NULL DEFAULT gen_random_uuid(),
    origen_registro VARCHAR(15) NOT NULL DEFAULT 'EN_LINEA',
    estado_sincronizacion VARCHAR(15)
        NOT NULL DEFAULT 'SINCRONIZADO',
    sincronizado_en TIMESTAMPTZ,
    observacion VARCHAR(250),

    CONSTRAINT pk_registro_asistencia PRIMARY KEY (id_registro),
    CONSTRAINT uq_asistencia_matricula_fecha
        UNIQUE (id_matricula, fecha_asistencia),
    CONSTRAINT uq_asistencia_sincronizacion
        UNIQUE (uuid_sincronizacion),
    CONSTRAINT ck_asistencia_estado
        CHECK (
            estado IN ('PUNTUAL', 'TARDANZA', 'INASISTENCIA')
        ),
    CONSTRAINT ck_asistencia_orden_horas
        CHECK (registrado_en >= capturado_en),
    CONSTRAINT ck_asistencia_metodo
        CHECK (
            metodo_registro IN ('QR', 'MANUAL', 'AUTOMATICO')
        ),
    CONSTRAINT ck_asistencia_origen
        CHECK (
            origen_registro IN ('EN_LINEA', 'SIN_CONEXION')
        ),
    CONSTRAINT ck_asistencia_sincronizacion
        CHECK (
            estado_sincronizacion IN (
                'PENDIENTE', 'SINCRONIZADO', 'ERROR'
            )
        ),
    CONSTRAINT fk_asistencia_matricula
        FOREIGN KEY (id_matricula)
        REFERENCES matricula(id_matricula)
        ON DELETE RESTRICT,
    CONSTRAINT fk_asistencia_usuario
        FOREIGN KEY (id_usuario_registro)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

CREATE TABLE justificacion (
    id_justificacion BIGINT GENERATED ALWAYS AS IDENTITY,
    id_registro BIGINT NOT NULL,
    id_usuario_registro BIGINT NOT NULL,
    id_usuario_revisor BIGINT,
    motivo VARCHAR(100) NOT NULL,
    observacion TEXT,
    documento_url VARCHAR(500),
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    presentada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revisada_en TIMESTAMPTZ,
    comentario_revision TEXT,

    CONSTRAINT pk_justificacion PRIMARY KEY (id_justificacion),
    -- la unicidad se controla con un indice parcial mas abajo:
    -- solo puede existir una justificacion vigente por registro,
    -- pero una rechazada puede volver a presentarse
    CONSTRAINT ck_justificacion_estado
        CHECK (
            estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')
        ),
    CONSTRAINT ck_justificacion_revision
        CHECK (
            revisada_en IS NULL
            OR revisada_en >= presentada_en
        ),
    CONSTRAINT fk_justificacion_registro
        FOREIGN KEY (id_registro)
        REFERENCES registro_asistencia(id_registro)
        ON DELETE RESTRICT,
    CONSTRAINT fk_justificacion_usuario_registro
        FOREIGN KEY (id_usuario_registro)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT,
    CONSTRAINT fk_justificacion_usuario_revisor
        FOREIGN KEY (id_usuario_revisor)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

-- =====================================================
-- ALERTAS Y NOTIFICACIONES
-- =====================================================

CREATE TABLE alerta (
    id_alerta BIGINT GENERATED ALWAYS AS IDENTITY,
    id_matricula BIGINT NOT NULL,
    id_usuario_atencion BIGINT,
    tipo VARCHAR(20) NOT NULL,
    nivel VARCHAR(15) NOT NULL,
    cantidad_acumulada SMALLINT NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    generada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atendida_en TIMESTAMPTZ,
    observacion TEXT,

    CONSTRAINT pk_alerta PRIMARY KEY (id_alerta),
    CONSTRAINT ck_alerta_tipo
        CHECK (tipo IN ('TARDANZAS', 'INASISTENCIAS')),
    CONSTRAINT ck_alerta_nivel
        CHECK (
            nivel IN ('INFORMATIVA', 'PREVENTIVA', 'CRITICA')
        ),
    CONSTRAINT ck_alerta_cantidad
        CHECK (cantidad_acumulada > 0),
    CONSTRAINT ck_alerta_estado
        CHECK (
            estado IN ('PENDIENTE', 'EN_ATENCION', 'ATENDIDA')
        ),
    CONSTRAINT ck_alerta_atencion
        CHECK (
            atendida_en IS NULL
            OR atendida_en >= generada_en
        ),
    CONSTRAINT fk_alerta_matricula
        FOREIGN KEY (id_matricula)
        REFERENCES matricula(id_matricula)
        ON DELETE RESTRICT,
    CONSTRAINT fk_alerta_usuario
        FOREIGN KEY (id_usuario_atencion)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);

CREATE TABLE notificacion (
    id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY,
    id_registro BIGINT NOT NULL,
    id_apoderado BIGINT NOT NULL,
    canal VARCHAR(15) NOT NULL,
    destino VARCHAR(254) NOT NULL,
    mensaje TEXT NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    numero_intentos SMALLINT NOT NULL DEFAULT 0,
    creada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enviada_en TIMESTAMPTZ,
    detalle_error TEXT,

    CONSTRAINT pk_notificacion PRIMARY KEY (id_notificacion),
    CONSTRAINT ck_notificacion_canal
        CHECK (canal IN ('WHATSAPP', 'CORREO', 'SMS')),
    CONSTRAINT ck_notificacion_estado
        CHECK (estado IN ('PENDIENTE', 'ENVIADA', 'FALLIDA')),
    CONSTRAINT ck_notificacion_intentos
        CHECK (numero_intentos >= 0),
    CONSTRAINT ck_notificacion_envio
        CHECK (
            enviada_en IS NULL
            OR enviada_en >= creada_en
        ),
    CONSTRAINT fk_notificacion_registro
        FOREIGN KEY (id_registro)
        REFERENCES registro_asistencia(id_registro)
        ON DELETE RESTRICT,
    CONSTRAINT fk_notificacion_apoderado
        FOREIGN KEY (id_apoderado)
        REFERENCES apoderado(id_apoderado)
        ON DELETE RESTRICT
);

-- =====================================================
-- ÍNDICES DE CONSULTA Y REGLAS PARCIALES
-- =====================================================

CREATE INDEX idx_usuario_rol
    ON usuario (id_rol);

CREATE INDEX idx_auditoria_usuario_fecha
    ON auditoria (id_usuario, realizado_en DESC);

CREATE INDEX idx_auditoria_registro
    ON auditoria (tabla_afectada, id_registro_afectado);

CREATE INDEX idx_estudiante_apellidos_nombres
    ON estudiante (apellidos, nombres);

CREATE INDEX idx_estudiante_apoderado_apoderado
    ON estudiante_apoderado (id_apoderado);

CREATE UNIQUE INDEX uq_apoderado_principal_estudiante
    ON estudiante_apoderado (id_estudiante)
    WHERE es_principal = TRUE AND activo = TRUE;

CREATE INDEX idx_matricula_seccion_periodo
    ON matricula (id_seccion, id_periodo);

CREATE INDEX idx_matricula_periodo_estado
    ON matricula (id_periodo, estado);

CREATE UNIQUE INDEX uq_periodo_activo
    ON periodo_academico (estado)
    WHERE estado = 'ACTIVO';

CREATE UNIQUE INDEX uq_configuracion_activa_periodo
    ON configuracion (id_periodo)
    WHERE activo = TRUE;

CREATE INDEX idx_credencial_estudiante_estado
    ON credencial (id_estudiante, estado);

CREATE UNIQUE INDEX uq_credencial_activa_estudiante
    ON credencial (id_estudiante)
    WHERE estado = 'ACTIVA';

CREATE INDEX idx_asistencia_capturado
    ON registro_asistencia (fecha_asistencia, capturado_en);

CREATE INDEX idx_asistencia_fecha_estado
    ON registro_asistencia (fecha_asistencia, estado);

CREATE INDEX idx_asistencia_sincronizacion_pendiente
    ON registro_asistencia (registrado_en)
    WHERE estado_sincronizacion = 'PENDIENTE';

CREATE UNIQUE INDEX uq_justificacion_vigente
    ON justificacion (id_registro)
    WHERE estado IN ('PENDIENTE', 'APROBADA');

CREATE INDEX idx_justificacion_pendiente
    ON justificacion (presentada_en)
    WHERE estado = 'PENDIENTE';

CREATE INDEX idx_justificacion_revisor
    ON justificacion (id_usuario_revisor);

CREATE INDEX idx_alerta_estado_fecha
    ON alerta (estado, generada_en DESC);

CREATE INDEX idx_alerta_matricula
    ON alerta (id_matricula, generada_en DESC);

CREATE INDEX idx_notificacion_por_procesar
    ON notificacion (estado, creada_en)
    WHERE estado IN ('PENDIENTE', 'FALLIDA');

CREATE INDEX idx_notificacion_apoderado_fecha
    ON notificacion (id_apoderado, creada_en DESC);

CREATE INDEX idx_notificacion_registro
    ON notificacion (id_registro);

COMMIT;