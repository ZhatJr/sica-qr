package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.ResumenDia;
import pe.edu.utp.sicaqr.dto.SeccionDto;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.servicio.ServicioAsistencia;

import java.time.LocalDate;
import java.util.List;

/** API de indicadores del panel principal (V02) y catálogo de secciones. */
@RestController
@RequestMapping("/api")
public class PanelControlador {

    private final ServicioAsistencia servicioAsistencia;

    public PanelControlador(ServicioAsistencia servicioAsistencia) {
        this.servicioAsistencia = servicioAsistencia;
    }

    /** GET /api/panel/resumen?fecha=2026-10-23   (todos los roles) */
    @GetMapping("/panel/resumen")
    public ResumenDia resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            HttpSession sesion) {
        ControlAcceso.usuarioActual(sesion);
        return servicioAsistencia.resumenDelDia(fecha);
    }

    /** GET /api/secciones */
    @GetMapping("/secciones")
    public List<SeccionDto> secciones(HttpSession sesion) {
        ControlAcceso.usuarioActual(sesion);
        return servicioAsistencia.listarSecciones();
    }
}
