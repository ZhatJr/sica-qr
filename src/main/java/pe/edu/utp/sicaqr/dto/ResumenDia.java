package pe.edu.utp.sicaqr.dto;

import java.time.LocalDate;

/** Indicadores de asistencia de un día (V02). */
public record ResumenDia(
        LocalDate fecha,
        long matriculados,
        long puntuales,
        long tardanzas,
        long inasistencias,
        long sinRegistro) {
}
