package pe.edu.utp.sicaqr.seguridad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.utp.sicaqr.dto.MensajeError;

/**
 * Convierte los errores en respuestas JSON con el codigo HTTP correcto.
 *
 * Seguridad: ante un error inesperado se devuelve un mensaje generico.
 * El detalle tecnico solo va al log, nunca al usuario (no se exponen
 * nombres de tablas ni consultas SQL).
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<MensajeError> reglaNegocio(ReglaNegocioException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<MensajeError> noAutenticado(NoAutenticadoException e) {
        return responder(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<MensajeError> accesoDenegado(AccesoDenegadoException e) {
        return responder(HttpStatus.FORBIDDEN, e.getMessage());
    }

    // JSON mal formado o fecha con formato incorrecto
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<MensajeError> datosInvalidos(Exception e) {
        return responder(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen el formato correcto");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeError> inesperado(Exception e) {
        // Errores propios de Spring (ruta inexistente, metodo no permitido...)
        // conservan su codigo HTTP original (404, 405...)
        if (e instanceof ErrorResponse respuesta) {
            HttpStatus estado = HttpStatus.valueOf(respuesta.getStatusCode().value());
            return responder(estado, estado.getReasonPhrase());
        }
        log.error("Error inesperado", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error en el servidor. Intente nuevamente.");
    }

    private ResponseEntity<MensajeError> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new MensajeError(estado.value(), mensaje));
    }
}
