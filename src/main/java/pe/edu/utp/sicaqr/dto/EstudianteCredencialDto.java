package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/** Estudiante con su sección y su credencial activa, si la tiene (V03). */
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
