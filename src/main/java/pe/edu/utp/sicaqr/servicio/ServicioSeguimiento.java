package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.AlertaDto;
import pe.edu.utp.sicaqr.dto.JustificacionDto;
import pe.edu.utp.sicaqr.dto.NotificacionDto;

import java.util.List;

/**
 * Consulta de justificaciones, alertas y notificaciones (V06, V07, V08).
 *
 * En este avance (40 %) estas pantallas solo MUESTRAN datos.
 * Registrar y aprobar justificaciones (RF-05, RF-06), generar alertas (RF-08)
 * y enviar notificaciones (RF-09) se implementan en el cuarto avance.
 */
public interface ServicioSeguimiento {

    List<JustificacionDto> listarJustificaciones();

    List<AlertaDto> listarAlertas();

    List<NotificacionDto> listarNotificaciones();
}
