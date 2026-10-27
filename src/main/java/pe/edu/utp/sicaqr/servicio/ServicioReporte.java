package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.FilaAsistencia;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * SOLID · Abierto/Cerrado (OCP).
 *
 * Hoy existe una implementacion: ServicioReporteExcel (Apache POI).
 * Si se pide el reporte en PDF (el informe menciona iText), se crea otra
 * clase que implemente esta interfaz, SIN modificar la de Excel que ya funciona.
 */
public interface ServicioReporte {

    byte[] generarReporteAsistencia(List<FilaAsistencia> filas, LocalDate desde, LocalDate hasta)
            throws IOException;

    String getExtension();

    String getTipoContenido();
}
