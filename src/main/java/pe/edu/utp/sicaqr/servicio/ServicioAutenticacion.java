package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.UsuarioSesion;

/** Inicio y cierre de sesión del personal (RF-12). */
public interface ServicioAutenticacion {

    /**
     * Valida las credenciales y registra el acceso.
     *
     * @return datos del usuario autenticado
     */
    UsuarioSesion iniciarSesion(String nombreUsuario, String clave, String ip);

    /** Registra el cierre de sesión en la bitácora. */
    void cerrarSesion(UsuarioSesion usuario, String ip);
}
