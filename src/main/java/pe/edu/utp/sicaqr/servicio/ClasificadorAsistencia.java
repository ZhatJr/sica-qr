package pe.edu.utp.sicaqr.servicio;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Clasificación del ingreso según el horario configurado (RF-02).
 * Con entrada 07:30, tolerancia de 10 minutos y corte a las 09:00:
 * hasta 07:40 es PUNTUAL, de 07:41 a 08:59 es TARDANZA y desde 09:00 es INASISTENCIA.
 */
public final class ClasificadorAsistencia {

    public static final String PUNTUAL = "PUNTUAL";
    public static final String TARDANZA = "TARDANZA";
    public static final String INASISTENCIA = "INASISTENCIA";

    private ClasificadorAsistencia() {
    }

    /**
     * Clasifica un ingreso.
     *
     * @param horaCaptura       hora de lectura del QR en el dispositivo
     * @param horaEntrada       hora oficial de entrada
     * @param toleranciaMinutos minutos de tolerancia
     * @param horaCorte         hora a partir de la cual el ingreso cuenta como inasistencia
     * @return PUNTUAL, TARDANZA o INASISTENCIA
     */
    public static String clasificar(LocalTime horaCaptura, LocalTime horaEntrada,
                                    int toleranciaMinutos, LocalTime horaCorte) {

        // Se comparan minutos completos (07:40:59 cuenta como 07:40)
        LocalTime hora = horaCaptura.truncatedTo(ChronoUnit.MINUTES);
        LocalTime limitePuntual = horaEntrada.plusMinutes(toleranciaMinutos);

        if (!hora.isAfter(limitePuntual)) {
            return PUNTUAL;
        }
        if (hora.isBefore(horaCorte)) {
            return TARDANZA;
        }
        return INASISTENCIA;
    }
}
