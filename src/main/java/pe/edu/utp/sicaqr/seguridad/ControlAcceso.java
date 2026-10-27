package pe.edu.utp.sicaqr.seguridad;

import jakarta.servlet.http.HttpSession;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.util.Arrays;

/**
 * SEGURIDAD · Control de acceso por rol (RF-12).
 *
 * Cada controlador llama a exigirRol(...) al inicio. Si no hay sesion
 * responde 401; si el rol no esta permitido responde 403.
 * La verificacion se hace en el servidor: ocultar un boton en la
 * pantalla no basta, porque cualquiera puede llamar a la API directamente.
 */
public final class ControlAcceso {

    public static final String CLAVE_SESION = "usuario";

    private ControlAcceso() {
    }

    /** Devuelve el usuario de la sesion o lanza 401. */
    public static UsuarioSesion usuarioActual(HttpSession sesion) {
        Object usuario = sesion.getAttribute(CLAVE_SESION);
        if (usuario == null) {
            throw new NoAutenticadoException();
        }
        return (UsuarioSesion) usuario;
    }

    /** Verifica que el usuario tenga alguno de los roles indicados. */
    public static UsuarioSesion exigirRol(HttpSession sesion, String... rolesPermitidos) {
        UsuarioSesion usuario = usuarioActual(sesion);
        if (!Arrays.asList(rolesPermitidos).contains(usuario.rol())) {
            throw new AccesoDenegadoException();
        }
        return usuario;
    }
}
