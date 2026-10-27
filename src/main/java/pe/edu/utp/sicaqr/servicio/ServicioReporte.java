package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.FilaAsistencia;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Generación del reporte de asistencia (RF-07).
 * Cada formato de salida es una implementación de esta interfaz.
 */
public interface ServicioReporte {

    /**
     * Genera el archivo del reporte.
     *
     * @param filas registros a incluir
     * @param desde fecha inicial del rango
     * @param hasta fecha final del rango
     * @return contenido del archivo
     */
    byte[] generarReporteAsistencia(List<FilaAsistencia> filas, LocalDate desde, LocalDate hasta)
            throws IOException;

    /** Extensión del archivo generado. */
    String getExtension();

    /** Tipo MIME del archivo generado. */
    String getTipoContenido();
}
