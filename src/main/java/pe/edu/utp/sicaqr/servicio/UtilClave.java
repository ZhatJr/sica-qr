package pe.edu.utp.sicaqr.servicio;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Cifrado de contraseñas con SHA-256 y sal, usando Google Guava (RNF-04).
 * Las contraseñas se almacenan solo como hash y se verifican comparando hashes.
 */
public final class UtilClave {

    private static final String SAL = "SICA-QR-14762";

    private UtilClave() {
    }

    /**
     * Calcula el hash de una contraseña.
     *
     * @param clave contraseña en texto plano
     * @return hash SHA-256 en hexadecimal (64 caracteres)
     */
    public static String cifrar(String clave) {
        Preconditions.checkNotNull(clave, "La clave no puede ser nula");

        return Hashing.sha256()
                .hashString(SAL + clave, StandardCharsets.UTF_8)
                .toString();
    }

    /** Indica si una contraseña corresponde al hash almacenado. */
    public static boolean coincide(String claveEscrita, String hashGuardado) {
        return cifrar(claveEscrita).equals(hashGuardado);
    }
}
