package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.FilaAsistencia;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.seguridad.Roles;
import pe.edu.utp.sicaqr.servicio.ServicioAsistencia;
import pe.edu.utp.sicaqr.servicio.ServicioAuditoria;
import pe.edu.utp.sicaqr.servicio.ServicioReporte;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** API de descarga del reporte de asistencia (RF-07). Acceso: DOCENTE y DIRECCION. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {

    private final ServicioAsistencia servicioAsistencia;
    private final ServicioReporte servicioReporte;
    private final ServicioAuditoria auditoria;

    public ReporteControlador(ServicioAsistencia servicioAsistencia,
                              ServicioReporte servicioReporte,
                              ServicioAuditoria auditoria) {
        this.servicioAsistencia = servicioAsistencia;
        this.servicioReporte = servicioReporte;
        this.auditoria = auditoria;
    }

    /** GET /api/reportes/asistencia?idSeccion=3&desde=2026-10-01&hasta=2026-10-31 */
    @GetMapping("/asistencia")
    public ResponseEntity<byte[]> reporteAsistencia(
            @RequestParam(required = false) Long idSeccion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpSession sesion, HttpServletRequest request) throws IOException {

        UsuarioSesion usuario = ControlAcceso.exigirRol(sesion, Roles.DOCENTE, Roles.DIRECCION);

        LocalDate inicio = desde != null ? desde : LocalDate.now();
        LocalDate fin = hasta != null ? hasta : LocalDate.now();

        List<FilaAsistencia> filas = servicioAsistencia.consultar(idSeccion, inicio, fin);
        byte[] archivo = servicioReporte.generarReporteAsistencia(filas, inicio, fin);

        auditoria.registrar(usuario.idUsuario(), "EXPORT", "registro_asistencia", null,
                Map.of("formato", servicioReporte.getExtension(), "desde", inicio.toString(),
                        "hasta", fin.toString(), "filas", filas.size()),
                request.getRemoteAddr());

        String nombre = "asistencia_" + inicio + "_" + fin + "." + servicioReporte.getExtension();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.parseMediaType(servicioReporte.getTipoContenido()))
                .body(archivo);
    }
}
