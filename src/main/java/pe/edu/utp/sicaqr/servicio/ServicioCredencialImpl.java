package pe.edu.utp.sicaqr.servicio;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sicaqr.dto.EstudianteCredencialDto;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.modelo.Credencial;
import pe.edu.utp.sicaqr.modelo.Estudiante;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;
import pe.edu.utp.sicaqr.repositorio.CredencialRepositorio;
import pe.edu.utp.sicaqr.repositorio.EstudianteRepositorio;
import pe.edu.utp.sicaqr.repositorio.MatriculaRepositorio;
import pe.edu.utp.sicaqr.repositorio.PeriodoAcademicoRepositorio;
import pe.edu.utp.sicaqr.repositorio.UsuarioRepositorio;
import pe.edu.utp.sicaqr.seguridad.ReglaNegocioException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Gestion de credenciales QR (RF-11).
 *
 * El QR contiene solo el token (un UUID aleatorio). No lleva nombre ni DNI:
 * si alguien lo lee con su celular, solo ve un codigo sin significado
 * (Ley N.o 29733 de Proteccion de Datos Personales).
 */
@Service
public class ServicioCredencialImpl implements ServicioCredencial {

    private static final int TAMANIO_QR = 300;

    private final CredencialRepositorio credencialRepositorio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final PeriodoAcademicoRepositorio periodoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioAuditoria auditoria;

    public ServicioCredencialImpl(CredencialRepositorio credencialRepositorio,
                                  EstudianteRepositorio estudianteRepositorio,
                                  MatriculaRepositorio matriculaRepositorio,
                                  PeriodoAcademicoRepositorio periodoRepositorio,
                                  UsuarioRepositorio usuarioRepositorio,
                                  ServicioAuditoria auditoria) {
        this.credencialRepositorio = credencialRepositorio;
        this.estudianteRepositorio = estudianteRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.periodoRepositorio = periodoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.auditoria = auditoria;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteCredencialDto> listarEstudiantes() {
        Optional<PeriodoAcademico> periodo = periodoRepositorio.findByEstado("ACTIVO");
        List<EstudianteCredencialDto> lista = new ArrayList<>();

        for (Estudiante e : estudianteRepositorio.findByActivoTrueOrderByApellidosAscNombresAsc()) {
            lista.add(aDto(e, periodo.orElse(null)));
        }
        return lista;
    }

    @Override
    @Transactional
    public EstudianteCredencialDto emitir(Long idEstudiante, UsuarioSesion usuario, String ip) {
        if (idEstudiante == null) {
            throw new ReglaNegocioException("Seleccione un estudiante");
        }
        Estudiante estudiante = estudianteRepositorio.findById(idEstudiante)
                .orElseThrow(() -> new ReglaNegocioException("El estudiante no existe"));

        // Solo una credencial activa por estudiante (indice uq_credencial_activa_estudiante)
        if (credencialRepositorio.findByEstudianteAndEstado(estudiante, "ACTIVA").isPresent()) {
            throw new ReglaNegocioException(
                    "El estudiante ya tiene una credencial activa. Revóquela antes de emitir otra.");
        }

        Credencial credencial = new Credencial();   // el token UUID se genera en la entidad
        credencial.setEstudiante(estudiante);
        credencial.setUsuarioEmisor(usuarioRepositorio.getReferenceById(usuario.idUsuario()));
        credencialRepositorio.save(credencial);

        auditoria.registrar(usuario.idUsuario(), "INSERT", "credencial", credencial.getIdCredencial(),
                Map.of("estudiante", estudiante.getCodigo(), "accion", "emision"), ip);

        return aDto(estudiante, periodoRepositorio.findByEstado("ACTIVO").orElse(null));
    }

    @Override
    @Transactional
    public void revocar(Long idCredencial, String motivo, UsuarioSesion usuario, String ip) {
        Credencial credencial = credencialRepositorio.findById(idCredencial)
                .orElseThrow(() -> new ReglaNegocioException("La credencial no existe"));

        if (!"ACTIVA".equals(credencial.getEstado())) {
            throw new ReglaNegocioException("La credencial ya no está activa");
        }
        // Apache Commons: valida que el motivo no este vacio
        if (StringUtils.isBlank(motivo)) {
            throw new ReglaNegocioException("Indique el motivo de la revocación");
        }

        // No se borra: se cambia el estado (las bajas se manejan por estado, apartado 3.5 del informe)
        credencial.setEstado("REVOCADA");
        credencial.setRevocadaEn(LocalDateTime.now());
        credencial.setMotivoRevocacion(StringUtils.left(motivo.trim(), 200));
        credencial.setUsuarioRevocador(usuarioRepositorio.getReferenceById(usuario.idUsuario()));
        credencialRepositorio.save(credencial);

        auditoria.registrar(usuario.idUsuario(), "UPDATE", "credencial", idCredencial,
                Map.of("estado_anterior", "ACTIVA", "estado_nuevo", "REVOCADA",
                        "motivo", credencial.getMotivoRevocacion()), ip);
    }

    /** Genera la imagen PNG del codigo QR con la libreria ZXing. */
    @Override
    @Transactional(readOnly = true)
    public byte[] generarImagenQr(String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException("Token no válido");
        }
        // Solo se dibujan QR de credenciales activas
        credencialRepositorio.findByTokenUuidAndEstado(uuid, "ACTIVA")
                .orElseThrow(() -> new ReglaNegocioException("La credencial no está activa"));

        try {
            BitMatrix matriz = new QRCodeWriter().encode(uuid.toString(), BarcodeFormat.QR_CODE,
                    TAMANIO_QR, TAMANIO_QR, Map.of(EncodeHintType.MARGIN, 1));
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
            return salida.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el código QR", e);
        }
    }

    private EstudianteCredencialDto aDto(Estudiante e, PeriodoAcademico periodo) {
        Optional<Credencial> credencial = credencialRepositorio.findByEstudianteAndEstado(e, "ACTIVA");
        Optional<Matricula> matricula = periodo == null
                ? Optional.empty()
                : matriculaRepositorio.findByEstudianteAndPeriodoAndEstado(e, periodo, "ACTIVA");

        return new EstudianteCredencialDto(
                e.getIdEstudiante(),
                e.getCodigo(),
                e.getDni(),
                Formato.nombre(e),
                Formato.seccion(matricula.map(Matricula::getSeccion).orElse(null)),
                credencial.map(Credencial::getIdCredencial).orElse(null),
                credencial.map(c -> c.getTokenUuid().toString()).orElse(null),
                credencial.map(Credencial::getEmitidaEn).orElse(null));
    }
}
