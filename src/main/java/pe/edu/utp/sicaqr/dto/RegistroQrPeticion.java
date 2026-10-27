package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/**
 * Lectura de un código QR enviada desde el registro de ingreso (V04).
 *
 * @param token              texto leído del código QR
 * @param capturadoEn        fecha y hora de la lectura en el dispositivo
 * @param uuidSincronizacion identificador único del envío, evita duplicados al reenviar
 */
public record RegistroQrPeticion(String token, LocalDateTime capturadoEn, String uuidSincronizacion) {
}
