package pe.edu.utp.sicaqr.servicio;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sicaqr.dto.FilaAsistencia;
import pe.edu.utp.sicaqr.dto.RegistroDniPeticion;
import pe.edu.utp.sicaqr.dto.RegistroQrPeticion;
import pe.edu.utp.sicaqr.dto.ResultadoRegistro;
import pe.edu.utp.sicaqr.dto.ResumenDia;
import pe.edu.utp.sicaqr.dto.SeccionDto;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.modelo.CalendarioEscolar;
import pe.edu.utp.sicaqr.modelo.Configuracion;
import pe.edu.utp.sicaqr.modelo.Credencial;
import pe.edu.utp.sicaqr.modelo.Estudiante;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;
import pe.edu.utp.sicaqr.modelo.RegistroAsistencia;
import pe.edu.utp.sicaqr.repositorio.CalendarioEscolarRepositorio;
import pe.edu.utp.sicaqr.repositorio.ConfiguracionRepositorio;
import pe.edu.utp.sicaqr.repositorio.CredencialRepositorio;
import pe.edu.utp.sicaqr.repositorio.EstudianteRepositorio;
import pe.edu.utp.sicaqr.repositorio.MatriculaRepositorio;
import pe.edu.utp.sicaqr.repositorio.PeriodoAcademicoRepositorio;
import pe.edu.utp.sicaqr.repositorio.RegistroAsistenciaRepositorio;
import pe.edu.utp.sicaqr.repositorio.SeccionRepositorio;
import pe.edu.utp.sicaqr.repositorio.UsuarioRepositorio;
import pe.edu.utp.sicaqr.seguridad.ReglaNegocioException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Registro y consulta de asistencia.
 *
 * SOLID · Responsabilidad unica (SRP): esta clase solo maneja asistencia.
 *   - La regla puntual/tardanza esta en ClasificadorAsistencia.
 *   - El cifrado de claves esta en UtilClave.
 *   - La bitacora esta en ServicioAuditoria.
 *
 * SOLID · Inversion de dependencias (DIP): recibe los DAO por el constructor;
 *   no crea sus dependencias con "new".
 */
@Service
public class ServicioAsistenciaImpl implements ServicioAsistencia {

    private final CredencialRepositorio credencialRepositorio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final PeriodoAcademicoRepositorio periodoRepositorio;
    private final ConfiguracionRepositorio configuracionRepositorio;
    private final CalendarioEscolarRepositorio calendarioRepositorio;
    private final RegistroAsistenciaRepositorio registroRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioAuditoria auditoria;

    public ServicioAsistenciaImpl(CredencialRepositorio credencialRepositorio,
                                  EstudianteRepositorio estudianteRepositorio,
                                  MatriculaRepositorio matriculaRepositorio,
                                  PeriodoAcademicoRepositorio periodoRepositorio,
                                  ConfiguracionRepositorio configuracionRepositorio,
                                  CalendarioEscolarRepositorio calendarioRepositorio,
                                  RegistroAsistenciaRepositorio registroRepositorio,
                                  SeccionRepositorio seccionRepositorio,
                                  UsuarioRepositorio usuarioRepositorio,
                                  ServicioAuditoria auditoria) {
        this.credencialRepositorio = credencialRepositorio;
        this.estudianteRepositorio = estudianteRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.periodoRepositorio = periodoRepositorio;
        this.configuracionRepositorio = configuracionRepositorio;
        this.calendarioRepositorio = calendarioRepositorio;
        this.registroRepositorio = registroRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.auditoria = auditoria;
    }

    // =====================================================
    // REGISTRO DE INGRESO
    // =====================================================

    @Override
    @Transactional
    public ResultadoRegistro registrarPorQr(RegistroQrPeticion peticion, UsuarioSesion usuario, String ip) {

        // Apache Commons: valida que se haya leido algo
        if (peticion == null || StringUtils.isBlank(peticion.token())) {
            throw new ReglaNegocioException("No se leyó ningún código");
        }

        UUID token = convertirUuid(peticion.token().trim(),
                "El código leído no es una credencial de SICA-QR");

        // Idempotencia (RNF-05): si este mismo envio ya llego antes, se devuelve
        // el registro existente en vez de crear otro
        UUID uuidSincronizacion = StringUtils.isBlank(peticion.uuidSincronizacion())
                ? UUID.randomUUID()
                : convertirUuid(peticion.uuidSincronizacion().trim(), "Identificador de envío no válido");

        Optional<RegistroAsistencia> yaRecibido = registroRepositorio.findByUuidSincronizacion(uuidSincronizacion);
        if (yaRecibido.isPresent()) {
            return aResultado(yaRecibido.get(), "Este registro ya había sido recibido");
        }

        // DAO: busca la credencial activa con ese token
        Credencial credencial = credencialRepositorio.findByTokenUuidAndEstado(token, "ACTIVA")
                .orElseThrow(() -> new ReglaNegocioException(
                        "Credencial no válida o revocada. Use el registro por DNI."));

        return registrar(credencial.getEstudiante(), peticion.capturadoEn(), uuidSincronizacion,
                "QR", null, usuario, ip);
    }

    @Override
    @Transactional
    public ResultadoRegistro registrarPorDni(RegistroDniPeticion peticion, UsuarioSesion usuario, String ip) {

        String dni = peticion == null ? "" : StringUtils.trimToEmpty(peticion.dni());

        // Apache Commons: el DNI debe tener 8 digitos numericos
        if (dni.length() != 8 || !StringUtils.isNumeric(dni)) {
            throw new ReglaNegocioException("El DNI debe tener 8 dígitos");
        }

        Estudiante estudiante = estudianteRepositorio.findByDni(dni)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró un estudiante con ese DNI"));

        String observacion = StringUtils.defaultIfBlank(peticion.observacion(),
                "Registro por DNI: el estudiante no portó su credencial");

        return registrar(estudiante, null, UUID.randomUUID(), "MANUAL",
                StringUtils.left(observacion, 250), usuario, ip);
    }

    /** Pasos comunes al registro por QR y por DNI. */
    private ResultadoRegistro registrar(Estudiante estudiante, LocalDateTime capturadoEn, UUID uuidSincronizacion,
                                        String metodo, String observacion, UsuarioSesion usuario, String ip) {

        LocalDateTime ahora = LocalDateTime.now();

        // La hora de captura la envia el dispositivo. Si no llega, o llega con
        // una hora futura (reloj mal configurado), se usa la hora del servidor.
        if (capturadoEn == null || capturadoEn.isAfter(ahora)) {
            capturadoEn = ahora;
        }
        LocalDate fecha = capturadoEn.toLocalDate();

        PeriodoAcademico periodo = periodoRepositorio.findByEstado("ACTIVO")
                .orElseThrow(() -> new ReglaNegocioException("No hay un año escolar activo"));

        // Un feriado o fin de semana no es dia de clases
        Optional<CalendarioEscolar> dia = calendarioRepositorio.findByPeriodoAndFecha(periodo, fecha);
        if (dia.isPresent() && !dia.get().getEsLaborable()) {
            throw new ReglaNegocioException("El " + Formato.fecha(fecha) + " no es día de clases ("
                    + dia.get().getDescripcion() + ")");
        }

        Matricula matricula = matriculaRepositorio.findByEstudianteAndPeriodoAndEstado(estudiante, periodo, "ACTIVA")
                .orElseThrow(() -> new ReglaNegocioException(
                        Formato.nombre(estudiante) + " no tiene matrícula activa en el año escolar"));

        // Control de duplicados: un registro por estudiante por dia
        Optional<RegistroAsistencia> duplicado = registroRepositorio.findByMatriculaAndFechaAsistencia(matricula, fecha);
        if (duplicado.isPresent()) {
            throw new ReglaNegocioException(Formato.nombre(estudiante) + " ya registró su ingreso hoy a las "
                    + Formato.hora(duplicado.get().getCapturadoEn()));
        }

        Configuracion configuracion = configuracionRepositorio.findByPeriodoAndActivoTrue(periodo)
                .orElseThrow(() -> new ReglaNegocioException("No hay una configuración de horario activa"));

        // RF-02: se clasifica con la HORA DE CAPTURA, no con la hora actual del servidor
        String estado = ClasificadorAsistencia.clasificar(
                capturadoEn.toLocalTime(),
                configuracion.getHoraEntrada(),
                configuracion.getToleranciaMinutos(),
                configuracion.getHoraCorte());

        // Si se leyo hace mas de 2 minutos, el registro llego tarde por falta de conexion
        boolean sinConexion = Duration.between(capturadoEn, ahora).toMinutes() >= 2;

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setMatricula(matricula);
        registro.setUsuarioRegistro(usuarioRepositorio.getReferenceById(usuario.idUsuario()));
        registro.setFechaAsistencia(fecha);
        registro.setCapturadoEn(capturadoEn);
        registro.setRegistradoEn(ahora);
        registro.setEstado(estado);
        registro.setMetodoRegistro(metodo);
        registro.setUuidSincronizacion(uuidSincronizacion);
        registro.setOrigenRegistro(sinConexion ? "SIN_CONEXION" : "EN_LINEA");
        registro.setSincronizadoEn(sinConexion ? ahora : null);
        registro.setObservacion(observacion);
        registroRepositorio.save(registro);

        auditoria.registrar(usuario.idUsuario(), "INSERT", "registro_asistencia", registro.getIdRegistro(),
                Map.of("estudiante", estudiante.getCodigo(), "estado", estado, "metodo", metodo), ip);

        return aResultado(registro, mensajeSegunEstado(estado));
    }

    // =====================================================
    // CONSULTA (RF-04)
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<FilaAsistencia> consultar(Long idSeccion, LocalDate desde, LocalDate hasta) {
        LocalDate hoy = LocalDate.now();
        if (desde == null) desde = hoy;
        if (hasta == null) hasta = hoy;

        if (desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la final");
        }

        List<RegistroAsistencia> registros = (idSeccion == null)
                ? registroRepositorio.findByFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(desde, hasta)
                : registroRepositorio.findByMatriculaSeccionIdSeccionAndFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(
                        idSeccion, desde, hasta);

        return registros.stream().map(this::aFila).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDia resumenDelDia(LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();

        long matriculados = periodoRepositorio.findByEstado("ACTIVO")
                .map(p -> matriculaRepositorio.countByPeriodoAndEstado(p, "ACTIVA"))
                .orElse(0L);

        long puntuales = registroRepositorio.countByFechaAsistenciaAndEstado(fecha, ClasificadorAsistencia.PUNTUAL);
        long tardanzas = registroRepositorio.countByFechaAsistenciaAndEstado(fecha, ClasificadorAsistencia.TARDANZA);
        long faltas = registroRepositorio.countByFechaAsistenciaAndEstado(fecha, ClasificadorAsistencia.INASISTENCIA);
        long sinRegistro = Math.max(0, matriculados - registroRepositorio.countByFechaAsistencia(fecha));

        return new ResumenDia(fecha, matriculados, puntuales, tardanzas, faltas, sinRegistro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionDto> listarSecciones() {
        return seccionRepositorio.findByActivoTrueOrderByIdSeccionAsc().stream()
                .map(s -> new SeccionDto(s.getIdSeccion(), Formato.seccion(s)))
                .toList();
    }

    // =====================================================
    // CONVERSIONES
    // =====================================================

    private UUID convertirUuid(String texto, String mensajeError) {
        try {
            return UUID.fromString(texto);
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException(mensajeError);
        }
    }

    private String mensajeSegunEstado(String estado) {
        return switch (estado) {
            case ClasificadorAsistencia.PUNTUAL -> "Ingreso puntual registrado";
            case ClasificadorAsistencia.TARDANZA -> "Ingreso registrado como tardanza";
            default -> "Llegó después de la hora de corte: se registra como inasistencia";
        };
    }

    private ResultadoRegistro aResultado(RegistroAsistencia r, String mensaje) {
        Estudiante e = r.getMatricula().getEstudiante();
        return new ResultadoRegistro(
                r.getIdRegistro(),
                e.getCodigo(),
                Formato.nombre(e),
                Formato.seccion(r.getMatricula().getSeccion()),
                e.getFotoUrl(),
                Formato.hora(r.getCapturadoEn()),
                r.getEstado(),
                r.getMetodoRegistro(),
                mensaje);
    }

    private FilaAsistencia aFila(RegistroAsistencia r) {
        Estudiante e = r.getMatricula().getEstudiante();
        return new FilaAsistencia(
                r.getIdRegistro(),
                r.getFechaAsistencia(),
                e.getCodigo(),
                Formato.nombre(e),
                Formato.seccion(r.getMatricula().getSeccion()),
                Formato.hora(r.getCapturadoEn()),
                r.getEstado(),
                r.getMetodoRegistro(),
                r.getOrigenRegistro());
    }
}
