package pe.edu.utp.sicaqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * SICA-QR: Sistema de Control de Asistencia Escolar mediante Código QR.
 * I.E. N.° 14762 "Luciano Castillo Colonna", La Huaca, Paita, Piura.
 */
@SpringBootApplication
public class SicaQrApplication {

    public static void main(String[] args) {
        // Zona horaria del sistema: Perú
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        SpringApplication.run(SicaQrApplication.class, args);
    }
}
