package pe.edu.utp.sicaqr.seguridad;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.util.List;

/**
 * Usuario autenticado en Spring Security (RF-12).
 * Los permisos por rol se definen en {@link ConfiguracionSeguridad}; esta clase
 * registra al usuario al iniciar sesión y lo entrega a los controladores.
 */
public final class ControlAcceso {

    private ControlAcceso() {
    }

    /**
     * Registra al usuario en Spring Security y guarda la autenticación en la sesión.
     * El rol se registra con el prefijo ROLE_ que exige Spring Security.
     */
    public static void registrarAutenticacion(UsuarioSesion usuario, HttpSession sesion) {
        Authentication autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                usuario, null, List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol())));

        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacion);
        SecurityContextHolder.setContext(contexto);
        sesion.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, contexto);
    }

    /**
     * Devuelve el usuario autenticado.
     *
     * @throws NoAutenticadoException si no hay sesión
     */
    public static UsuarioSesion usuarioActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioSesion usuario)) {
            throw new NoAutenticadoException();
        }
        return usuario;
    }

    /** Indica si la petición actual tiene un usuario autenticado. */
    public static boolean haySesion() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioSesion;
    }
}
