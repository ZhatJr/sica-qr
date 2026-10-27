package pe.edu.utp.sicaqr.dto;

/** Resultado de un registro de ingreso (V04). */
public record ResultadoRegistro(
        Long idRegistro,
        String codigo,
        String nombreCompleto,
        String seccion,
        String fotoUrl,
        String hora,
        String estado,
        String metodo,
        String mensaje) {
}
