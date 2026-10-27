package pe.edu.utp.sicaqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * SICA-QR · Sistema de Control de Asistencia Escolar mediante Codigo QR
 * I.E. N.o 14762 "Luciano Castillo Colonna" - La Huaca, Paita, Piura
 *
 * Punto de arranque de la aplicacion.
 */
@SpringBootApplication
public class SicaQrApplication {

    public static void main(String[] args) {
        // Todas las horas del sistema se manejan en hora de Peru, sin importar
        // como este configurada la computadora donde se ejecute.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        SpringApplication.run(SicaQrApplication.class, args);
    }
}
