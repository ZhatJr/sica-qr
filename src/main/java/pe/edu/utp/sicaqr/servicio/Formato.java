package pe.edu.utp.sicaqr.servicio;

import org.apache.commons.lang3.StringUtils;
import pe.edu.utp.sicaqr.modelo.Estudiante;
import pe.edu.utp.sicaqr.modelo.Seccion;
import pe.edu.utp.sicaqr.modelo.Usuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Textos que se muestran en pantalla y en los reportes. */
public final class Formato {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formato() {
    }

    /** "Alvarado Sosa, Luciana Valeria" */
    public static String nombre(Estudiante e) {
        return e.getApellidos() + ", " + e.getNombres();
    }

    public static String nombre(Usuario u) {
        return u == null ? null : u.getNombres() + " " + u.getApellidos();
    }

    /** "Primaria 5.° A" */
    public static String seccion(Seccion s) {
        if (s == null) {
            return "Sin matrícula";
        }
        // Apache Commons: "PRIMARIA" -> "Primaria"
        String nivel = StringUtils.capitalize(StringUtils.lowerCase(s.getGrado().getNivelEducativo()));
        return nivel + " " + s.getGrado().getNombre() + " " + s.getNombre();
    }

    public static String hora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(HORA);
    }

    public static String fecha(java.time.LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }
}
