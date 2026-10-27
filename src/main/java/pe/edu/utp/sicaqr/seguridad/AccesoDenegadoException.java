package pe.edu.utp.sicaqr.seguridad;

/** El rol del usuario no tiene permiso para la operación. Se responde con HTTP 403. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("Su rol no tiene permiso para esta operación");
    }
}
