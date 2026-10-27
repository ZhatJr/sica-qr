package pe.edu.utp.sicaqr.seguridad;

/** El rol del usuario no tiene permiso para esta operacion. La API responde 403. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("Su rol no tiene permiso para esta operacion");
    }
}
