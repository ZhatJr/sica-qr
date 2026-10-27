package pe.edu.utp.sicaqr.seguridad;

/** Petición sin sesión iniciada. Se responde con HTTP 401. */
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException() {
        super("Debe iniciar sesión");
    }
}
