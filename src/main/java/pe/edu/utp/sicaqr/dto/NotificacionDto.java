package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/** Notificación para el seguimiento de envíos (V06). */
public record NotificacionDto(
        Long idNotificacion,
        String apoderado,
        String estudiante,
        String canal,
        String destino,
        String mensaje,
        String estado,
        Integer intentos,
        LocalDateTime creadaEn,
        String detalleError) {
}
