package pe.edu.utp.sicaqr.dto;

import java.time.LocalDate;

/** Una fila de la consulta de asistencia (V05) y del reporte Excel. */
public record FilaAsistencia(
        Long idRegistro,
        LocalDate fecha,
        String codigo,
        String estudiante,
        String seccion,
        String hora,
        String estado,
        String metodo,
        String origen) {
}
