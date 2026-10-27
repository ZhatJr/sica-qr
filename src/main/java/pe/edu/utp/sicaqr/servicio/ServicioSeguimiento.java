package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.AlertaDto;
import pe.edu.utp.sicaqr.dto.JustificacionDto;
import pe.edu.utp.sicaqr.dto.NotificacionDto;

import java.util.List;

/**
 * Consulta de justificaciones, alertas y notificaciones (V06, V07, V08).
 * Versión de solo lectura; el registro, la aprobación y el envío corresponden
 * a la siguiente versión (RF-05, RF-06, RF-08, RF-09).
 */
public interface ServicioSeguimiento {

    List<JustificacionDto> listarJustificaciones();

    List<AlertaDto> listarAlertas();

    List<NotificacionDto> listarNotificaciones();
}
