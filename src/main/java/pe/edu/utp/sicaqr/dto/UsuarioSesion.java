package pe.edu.utp.sicaqr.dto;

import java.io.Serializable;

/**
 * Datos del usuario que se guardan en la sesion despues de iniciar sesion.
 * No incluye la contrasena.
 */
public record UsuarioSesion(Long idUsuario, String nombreUsuario, String nombreCompleto, String rol)
        implements Serializable {
}
