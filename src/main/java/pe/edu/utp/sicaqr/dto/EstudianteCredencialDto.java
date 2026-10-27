package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/** Fila de la pantalla V03: estudiante con su credencial activa (si tiene). */
public record EstudianteCredencialDto(
        Long idEstudiante,
        String codigo,
        String dni,
        String nombreCompleto,
        String seccion,
        Long idCredencial,
        String token,
        LocalDateTime emitidaEn) {
}
