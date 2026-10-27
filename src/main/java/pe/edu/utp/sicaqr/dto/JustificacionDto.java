package pe.edu.utp.sicaqr.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Justificación para la bandeja de revisión (V07). */
public record JustificacionDto(
        Long idJustificacion,
        String estudiante,
        String seccion,
        LocalDate fechaFalta,
        String estadoAsistencia,
        String motivo,
        String estado,
        LocalDateTime presentadaEn,
        String revisor) {
}
