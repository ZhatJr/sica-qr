package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/** Alerta de acumulación (V08). */
public record AlertaDto(
        Long idAlerta,
        String estudiante,
        String seccion,
        String tipo,
        String nivel,
        Integer cantidad,
        String estado,
        LocalDateTime generadaEn) {
}
