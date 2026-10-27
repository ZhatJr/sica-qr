package pe.edu.utp.sicaqr.seguridad;

/**
 * Incumplimiento de una regla de negocio (credencial revocada, ingreso duplicado,
 * día no laborable, etc.). Se responde con HTTP 400.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
