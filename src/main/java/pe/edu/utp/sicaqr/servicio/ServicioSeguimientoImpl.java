package pe.edu.utp.sicaqr.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sicaqr.dto.AlertaDto;
import pe.edu.utp.sicaqr.dto.JustificacionDto;
import pe.edu.utp.sicaqr.dto.NotificacionDto;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.repositorio.AlertaRepositorio;
import pe.edu.utp.sicaqr.repositorio.JustificacionRepositorio;
import pe.edu.utp.sicaqr.repositorio.NotificacionRepositorio;

import java.util.List;

@Service
public class ServicioSeguimientoImpl implements ServicioSeguimiento {

    private final JustificacionRepositorio justificacionRepositorio;
    private final AlertaRepositorio alertaRepositorio;
    private final NotificacionRepositorio notificacionRepositorio;

    public ServicioSeguimientoImpl(JustificacionRepositorio justificacionRepositorio,
                                   AlertaRepositorio alertaRepositorio,
                                   NotificacionRepositorio notificacionRepositorio) {
        this.justificacionRepositorio = justificacionRepositorio;
        this.alertaRepositorio = alertaRepositorio;
        this.notificacionRepositorio = notificacionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JustificacionDto> listarJustificaciones() {
        return justificacionRepositorio.findAllByOrderByPresentadaEnDesc().stream()
                .map(j -> {
                    Matricula m = j.getRegistro().getMatricula();
                    return new JustificacionDto(
                            j.getIdJustificacion(),
                            Formato.nombre(m.getEstudiante()),
                            Formato.seccion(m.getSeccion()),
                            j.getRegistro().getFechaAsistencia(),
                            j.getRegistro().getEstado(),
                            j.getMotivo(),
                            j.getEstado(),
                            j.getPresentadaEn(),
                            Formato.nombre(j.getUsuarioRevisor()));
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDto> listarAlertas() {
        return alertaRepositorio.findAllByOrderByGeneradaEnDesc().stream()
                .map(a -> new AlertaDto(
                        a.getIdAlerta(),
                        Formato.nombre(a.getMatricula().getEstudiante()),
                        Formato.seccion(a.getMatricula().getSeccion()),
                        a.getTipo(),
                        a.getNivel(),
                        a.getCantidadAcumulada(),
                        a.getEstado(),
                        a.getGeneradaEn()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionDto> listarNotificaciones() {
        return notificacionRepositorio.findAllByOrderByCreadaEnDesc().stream()
                .map(n -> new NotificacionDto(
                        n.getIdNotificacion(),
                        n.getApoderado().getNombres() + " " + n.getApoderado().getApellidos(),
                        Formato.nombre(n.getRegistro().getMatricula().getEstudiante()),
                        n.getCanal(),
                        n.getDestino(),
                        n.getMensaje(),
                        n.getEstado(),
                        n.getNumeroIntentos(),
                        n.getCreadaEn(),
                        n.getDetalleError()))
                .toList();
    }
}
