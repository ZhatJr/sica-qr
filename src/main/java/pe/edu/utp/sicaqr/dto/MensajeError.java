package pe.edu.utp.sicaqr.dto;

/** Respuesta de la API cuando algo sale mal. */
public record MensajeError(int estado, String mensaje) {
}
