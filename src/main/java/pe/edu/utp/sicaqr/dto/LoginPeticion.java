package pe.edu.utp.sicaqr.dto;

/** Credenciales enviadas desde el inicio de sesión (V01). */
public record LoginPeticion(String usuario, String clave) {
}
