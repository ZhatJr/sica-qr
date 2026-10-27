package pe.edu.utp.sicaqr.seguridad;

/**
 * Error esperado por una regla del sistema: credencial revocada,
 * ingreso duplicado, dia no laborable, etc. La API responde 400.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
