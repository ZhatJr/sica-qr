package pe.edu.utp.sicaqr.dto;

/** Cuerpo de las respuestas de error de la API. */
public record MensajeError(int estado, String mensaje) {
}
