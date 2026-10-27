package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.EstudianteCredencialDto;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.util.List;

/**
 * SOLID · Segregacion de interfaces (ISP).
 * Solo operaciones de credenciales. Quien registra asistencia no depende de esta interfaz.
 */
public interface ServicioCredencial {

    List<EstudianteCredencialDto> listarEstudiantes();

    EstudianteCredencialDto emitir(Long idEstudiante, UsuarioSesion usuario, String ip);

    void revocar(Long idCredencial, String motivo, UsuarioSesion usuario, String ip);

    byte[] generarImagenQr(String token);
}
