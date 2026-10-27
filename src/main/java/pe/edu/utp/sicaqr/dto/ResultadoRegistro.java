package pe.edu.utp.sicaqr.dto;

/** Lo que la pantalla V04 muestra despues de registrar un ingreso. */
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
