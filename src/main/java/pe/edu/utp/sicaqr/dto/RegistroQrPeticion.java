package pe.edu.utp.sicaqr.dto;

import java.time.LocalDateTime;

/**
 * Lo que envia la pantalla de registro (V04) al escanear un QR.
 *
 * token              -> texto leido del codigo QR
 * capturadoEn        -> hora en que el dispositivo leyo el QR
 * uuidSincronizacion -> identificador unico del envio; si se reenvia, no se duplica
 */
public record RegistroQrPeticion(String token, LocalDateTime capturadoEn, String uuidSincronizacion) {
}
