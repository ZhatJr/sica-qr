package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.EstudianteCredencialDto;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.util.List;

/** Gestión de credenciales QR (RF-11). */
public interface ServicioCredencial {

    /** Estudiantes activos con su sección y su credencial activa. */
    List<EstudianteCredencialDto> listarEstudiantes();

    /** Emite una credencial si el estudiante no tiene una activa. */
    EstudianteCredencialDto emitir(Long idEstudiante, UsuarioSesion usuario, String ip);

    /** Revoca una credencial activa indicando el motivo. */
    void revocar(Long idCredencial, String motivo, UsuarioSesion usuario, String ip);

    /** Imagen PNG del código QR de una credencial activa. */
    byte[] generarImagenQr(String token);
}
