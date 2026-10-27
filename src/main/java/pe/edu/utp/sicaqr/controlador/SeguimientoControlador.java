package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.AlertaDto;
import pe.edu.utp.sicaqr.dto.JustificacionDto;
import pe.edu.utp.sicaqr.dto.NotificacionDto;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.seguridad.Roles;
import pe.edu.utp.sicaqr.servicio.ServicioSeguimiento;

import java.util.List;

/**
 * CONTROLADOR (MVC) · Pantallas V06, V07 y V08.
 * En este avance solo consultan datos (GET). La logica completa va en el cuarto avance.
 */
@RestController
@RequestMapping("/api")
public class SeguimientoControlador {

    private final ServicioSeguimiento servicioSeguimiento;

    public SeguimientoControlador(ServicioSeguimiento servicioSeguimiento) {
        this.servicioSeguimiento = servicioSeguimiento;
    }

    /** GET /api/justificaciones   (V07) */
    @GetMapping("/justificaciones")
    public List<JustificacionDto> justificaciones(HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.DOCENTE, Roles.DIRECCION);
        return servicioSeguimiento.listarJustificaciones();
    }

    /** GET /api/alertas   (V08) */
    @GetMapping("/alertas")
    public List<AlertaDto> alertas(HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.DOCENTE, Roles.DIRECCION);
        return servicioSeguimiento.listarAlertas();
    }

    /** GET /api/notificaciones   (V06) */
    @GetMapping("/notificaciones")
    public List<NotificacionDto> notificaciones(HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.ADMINISTRADOR, Roles.AUXILIAR);
        return servicioSeguimiento.listarNotificaciones();
    }
}
