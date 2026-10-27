package pe.edu.utp.sicaqr.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Fila de la bandeja de justificaciones (V07). */
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
