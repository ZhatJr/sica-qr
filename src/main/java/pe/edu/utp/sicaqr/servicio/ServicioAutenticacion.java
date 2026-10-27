package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.UsuarioSesion;

/**
 * SOLID · Inversion de dependencias (DIP).
 *
 * El controlador depende de esta interfaz, no de la clase que la implementa.
 * Si manana se cambia la forma de autenticar, el controlador no se toca.
 */
public interface ServicioAutenticacion {

    UsuarioSesion iniciarSesion(String nombreUsuario, String clave, String ip);

    void cerrarSesion(UsuarioSesion usuario, String ip);
}
