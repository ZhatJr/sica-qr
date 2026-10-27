package pe.edu.utp.sicaqr.servicio;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.modelo.Usuario;
import pe.edu.utp.sicaqr.repositorio.UsuarioRepositorio;
import pe.edu.utp.sicaqr.seguridad.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Implementación del inicio de sesión (RF-12, RNF-04).
 * Compara contraseñas cifradas, responde con un mensaje genérico ante
 * credenciales inválidas, bloquea la cuenta al quinto intento fallido y
 * registra cada intento en la bitácora.
 */
@Service
public class ServicioAutenticacionImpl implements ServicioAutenticacion {

    private static final int MAXIMO_INTENTOS = 5;
    private static final String MENSAJE_GENERICO = "Usuario o contraseña incorrectos";

    private final UsuarioRepositorio usuarioRepositorio;
    private final ServicioAuditoria auditoria;

    public ServicioAutenticacionImpl(UsuarioRepositorio usuarioRepositorio,
                                     ServicioAuditoria auditoria) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.auditoria = auditoria;
    }

    @Override
    // noRollbackFor: el contador de intentos se guarda aunque el acceso falle
    @Transactional(noRollbackFor = ReglaNegocioException.class)
    public UsuarioSesion iniciarSesion(String nombreUsuario, String clave, String ip) {

        if (StringUtils.isAnyBlank(nombreUsuario, clave)) {
            throw new ReglaNegocioException("Ingrese usuario y contraseña");
        }

        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario.trim())
                .orElseThrow(() -> new ReglaNegocioException(MENSAJE_GENERICO));

        if (!usuario.getActivo()) {
            throw new ReglaNegocioException(MENSAJE_GENERICO);
        }

        if (usuario.getIntentosFallidos() >= MAXIMO_INTENTOS) {
            throw new ReglaNegocioException(
                    "Cuenta bloqueada por intentos fallidos. Comuníquese con el administrador.");
        }

        if (!UtilClave.coincide(clave, usuario.getClaveHash())) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            usuarioRepositorio.save(usuario);
            auditoria.registrar(usuario.getIdUsuario(), "LOGIN", "usuario", usuario.getIdUsuario(),
                    Map.of("resultado", "fallido", "intento", usuario.getIntentosFallidos()), ip);
            throw new ReglaNegocioException(MENSAJE_GENERICO);
        }

        usuario.setIntentosFallidos(0);
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepositorio.save(usuario);
        auditoria.registrar(usuario.getIdUsuario(), "LOGIN", "usuario", usuario.getIdUsuario(),
                Map.of("resultado", "exitoso"), ip);

        return new UsuarioSesion(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getNombres() + " " + usuario.getApellidos(),
                usuario.getRol().getNombre());
    }

    @Override
    @Transactional
    public void cerrarSesion(UsuarioSesion usuario, String ip) {
        auditoria.registrar(usuario.idUsuario(), "LOGOUT", "usuario", usuario.idUsuario(),
                Map.of("resultado", "cierre de sesion"), ip);
    }
}
