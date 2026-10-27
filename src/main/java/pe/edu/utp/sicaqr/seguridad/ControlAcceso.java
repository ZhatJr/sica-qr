package pe.edu.utp.sicaqr.seguridad;

import jakarta.servlet.http.HttpSession;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.util.Arrays;

/**
 * Control de acceso por rol (RF-12).
 * Los controladores lo invocan al inicio de cada operación: sin sesión se
 * responde 401 y con un rol no autorizado, 403.
 */
public final class ControlAcceso {

    public static final String CLAVE_SESION = "usuario";

    private ControlAcceso() {
    }

    /**
     * Devuelve el usuario autenticado.
     *
     * @throws NoAutenticadoException si no hay sesión
     */
    public static UsuarioSesion usuarioActual(HttpSession sesion) {
        Object usuario = sesion.getAttribute(CLAVE_SESION);
        if (usuario == null) {
            throw new NoAutenticadoException();
        }
        return (UsuarioSesion) usuario;
    }

    /**
     * Verifica que el usuario tenga alguno de los roles indicados.
     *
     * @return el usuario autenticado
     * @throws AccesoDenegadoException si el rol no está autorizado
     */
    public static UsuarioSesion exigirRol(HttpSession sesion, String... rolesPermitidos) {
        UsuarioSesion usuario = usuarioActual(sesion);
        if (!Arrays.asList(rolesPermitidos).contains(usuario.rol())) {
            throw new AccesoDenegadoException();
        }
        return usuario;
    }
}
