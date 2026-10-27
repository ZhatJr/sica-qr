package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.LoginPeticion;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.servicio.ServicioAutenticacion;

/** API de inicio y cierre de sesión (V01, RF-12). */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionControlador {

    private static final int MINUTOS_INACTIVIDAD = 30;   // RNF-04

    private final ServicioAutenticacion servicioAutenticacion;

    public AutenticacionControlador(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    /** POST /api/auth/login  { "usuario": "admin", "clave": "admin123" } */
    @PostMapping("/login")
    public UsuarioSesion iniciarSesion(@RequestBody LoginPeticion peticion, HttpServletRequest request) {

        UsuarioSesion usuario = servicioAutenticacion.iniciarSesion(
                peticion.usuario(), peticion.clave(), request.getRemoteAddr());

        // Nueva sesión tras autenticarse (prevención de fijación de sesión)
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(ControlAcceso.CLAVE_SESION, usuario);
        sesion.setMaxInactiveInterval(MINUTOS_INACTIVIDAD * 60);

        return usuario;
    }

    /** POST /api/auth/logout */
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            Object usuario = sesion.getAttribute(ControlAcceso.CLAVE_SESION);
            if (usuario != null) {
                servicioAutenticacion.cerrarSesion((UsuarioSesion) usuario, request.getRemoteAddr());
            }
            sesion.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    /** GET /api/auth/sesion: usuario de la sesión actual. */
    @GetMapping("/sesion")
    public UsuarioSesion sesionActual(HttpSession sesion) {
        return ControlAcceso.usuarioActual(sesion);
    }
}
