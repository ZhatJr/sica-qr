package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.FilaAsistencia;
import pe.edu.utp.sicaqr.dto.RegistroDniPeticion;
import pe.edu.utp.sicaqr.dto.RegistroQrPeticion;
import pe.edu.utp.sicaqr.dto.ResultadoRegistro;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.seguridad.Roles;
import pe.edu.utp.sicaqr.servicio.ServicioAsistencia;

import java.time.LocalDate;
import java.util.List;

/** API de registro de ingreso (V04) y consulta de asistencia (V05). */
@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaControlador {

    private final ServicioAsistencia servicioAsistencia;

    public AsistenciaControlador(ServicioAsistencia servicioAsistencia) {
        this.servicioAsistencia = servicioAsistencia;
    }

    /**
     * POST /api/asistencias/qr   (RF-01, RF-02)
     * { "token": "7fc5e6a7-...", "capturadoEn": "2026-10-27T07:35:00", "uuidSincronizacion": "..." }
     */
    @PostMapping("/qr")
    public ResponseEntity<ResultadoRegistro> registrarPorQr(@RequestBody RegistroQrPeticion peticion,
                                                            HttpSession sesion, HttpServletRequest request) {
        UsuarioSesion usuario = ControlAcceso.exigirRol(sesion, Roles.AUXILIAR);
        ResultadoRegistro resultado = servicioAsistencia.registrarPorQr(peticion, usuario, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /** POST /api/asistencias/dni  { "dni": "81234501" }   (RF-10) */
    @PostMapping("/dni")
    public ResponseEntity<ResultadoRegistro> registrarPorDni(@RequestBody RegistroDniPeticion peticion,
                                                             HttpSession sesion, HttpServletRequest request) {
        UsuarioSesion usuario = ControlAcceso.exigirRol(sesion, Roles.AUXILIAR);
        ResultadoRegistro resultado = servicioAsistencia.registrarPorDni(peticion, usuario, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /** GET /api/asistencias?idSeccion=3&desde=2026-10-01&hasta=2026-10-31   (RF-04) */
    @GetMapping
    public List<FilaAsistencia> consultar(
            @RequestParam(required = false) Long idSeccion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.AUXILIAR, Roles.DOCENTE, Roles.DIRECCION);
        return servicioAsistencia.consultar(idSeccion, desde, hasta);
    }
}
