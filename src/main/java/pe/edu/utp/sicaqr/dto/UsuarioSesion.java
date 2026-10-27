package pe.edu.utp.sicaqr.dto;

import java.io.Serializable;

/** Datos del usuario autenticado que se guardan en la sesión. No incluye la contraseña. */
public record UsuarioSesion(Long idUsuario, String nombreUsuario, String nombreCompleto, String rol)
        implements Serializable {
}
