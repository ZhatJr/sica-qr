package pe.edu.utp.sicaqr.servicio;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * REGLA CENTRAL DEL SISTEMA · Clasificacion del ingreso (RF-02).
 *
 * Con entrada 07:30, tolerancia 10 y corte 09:00:
 *   hasta 07:40           -> PUNTUAL
 *   de 07:41 a 08:59      -> TARDANZA
 *   desde 09:00           -> INASISTENCIA
 *
 * Esta separada en su propia clase (SRP) para poder probarla sola,
 * sin base de datos. Ver ClasificadorAsistenciaTest (TDD).
 */
public final class ClasificadorAsistencia {

    public static final String PUNTUAL = "PUNTUAL";
    public static final String TARDANZA = "TARDANZA";
    public static final String INASISTENCIA = "INASISTENCIA";

    private ClasificadorAsistencia() {
    }

    /**
     * @param horaCaptura hora en que se leyo el QR (NO la hora en que llego al servidor)
     */
    public static String clasificar(LocalTime horaCaptura, LocalTime horaEntrada,
                                    int toleranciaMinutos, LocalTime horaCorte) {

        // Se comparan minutos completos: 07:40:59 cuenta como 07:40
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
