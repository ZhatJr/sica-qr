package pe.edu.utp.sicaqr.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas unitarias de {@link ClasificadorAsistencia} (RF-02).
 * Parámetros del criterio de verificación: entrada 07:30, tolerancia 10 minutos, corte 09:00.
 */
class ClasificadorAsistenciaTest {

    private static final LocalTime ENTRADA = LocalTime.of(7, 30);
    private static final int TOLERANCIA = 10;
    private static final LocalTime CORTE = LocalTime.of(9, 0);

    private String clasificar(int hora, int minuto) {
        return ClasificadorAsistencia.clasificar(LocalTime.of(hora, minuto), ENTRADA, TOLERANCIA, CORTE);
    }

    @Test
    @DisplayName("07:35 es PUNTUAL (ejemplo del informe)")
    void ingresoDentroDeLaToleranciaEsPuntual() {
        assertEquals("PUNTUAL", clasificar(7, 35));
    }

    @Test
    @DisplayName("07:40 exacto sigue siendo PUNTUAL: el limite se incluye")
    void elLimiteExactoEsPuntual() {
        assertEquals("PUNTUAL", clasificar(7, 40));
    }

    @Test
    @DisplayName("07:40 con 59 segundos sigue siendo PUNTUAL")
    void losSegundosNoCuentan() {
        assertEquals("PUNTUAL", ClasificadorAsistencia.clasificar(
                LocalTime.of(7, 40, 59), ENTRADA, TOLERANCIA, CORTE));
    }

    @Test
    @DisplayName("07:41 es TARDANZA")
    void unMinutoDespuesDelLimiteEsTardanza() {
        assertEquals("TARDANZA", clasificar(7, 41));
    }

    @Test
    @DisplayName("07:45 es TARDANZA (ejemplo del informe)")
    void ingresoDespuesDeLaToleranciaEsTardanza() {
        assertEquals("TARDANZA", clasificar(7, 45));
    }

    @Test
    @DisplayName("08:59 todavia es TARDANZA")
    void antesDelCorteEsTardanza() {
        assertEquals("TARDANZA", clasificar(8, 59));
    }

    @Test
    @DisplayName("09:00 o despues es INASISTENCIA")
    void desdeLaHoraDeCorteEsInasistencia() {
        assertEquals("INASISTENCIA", clasificar(9, 0));
        assertEquals("INASISTENCIA", clasificar(10, 15));
    }

    @Test
    @DisplayName("Sin conexion: leido 07:20 y sincronizado 09:30 queda PUNTUAL")
    void laHoraDeSincronizacionNoAfecta() {
        // Se clasifica con la hora de captura (07:20), no con la de llegada al servidor (09:30)
        LocalTime capturadoEn = LocalTime.of(7, 20);
        LocalTime registradoEn = LocalTime.of(9, 30);

        assertEquals("PUNTUAL",
                ClasificadorAsistencia.clasificar(capturadoEn, ENTRADA, TOLERANCIA, CORTE));
        assertEquals("INASISTENCIA",
                ClasificadorAsistencia.clasificar(registradoEn, ENTRADA, TOLERANCIA, CORTE),
                "Con la hora de llegada al servidor el ingreso quedaría como inasistencia");
    }
}
