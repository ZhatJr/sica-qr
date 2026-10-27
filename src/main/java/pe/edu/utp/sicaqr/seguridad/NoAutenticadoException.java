package pe.edu.utp.sicaqr.seguridad;

/** No hay sesion iniciada. La API responde 401. */
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException() {
        super("Debe iniciar sesion");
    }
}
