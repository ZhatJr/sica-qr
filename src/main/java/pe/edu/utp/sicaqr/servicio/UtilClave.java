package pe.edu.utp.sicaqr.servicio;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * LIBRERIA GOOGLE GUAVA · Cifrado de contrasenas (RNF-04).
 *
 * La contrasena nunca se guarda tal cual: se guarda su "hash" SHA-256.
 * Un hash no se puede revertir; para verificar, se cifra lo que escribe
 * el usuario y se compara con lo guardado.
 *
 * Se agrega una "sal" (texto fijo) antes de cifrar para que el hash de
 * "admin123" no coincida con las tablas de hashes conocidas en internet.
 *
 * El script 02_SICA_QR_datos_prueba.sql usa exactamente este mismo calculo.
 */
public final class UtilClave {

    private static final String SAL = "SICA-QR-14762";

    private UtilClave() {
    }

    public static String cifrar(String clave) {
        // Guava: valida que el parametro no sea nulo
        Preconditions.checkNotNull(clave, "La clave no puede ser nula");

        // Guava: calcula el SHA-256 y lo devuelve en hexadecimal (64 caracteres)
        return Hashing.sha256()
                .hashString(SAL + clave, StandardCharsets.UTF_8)
                .toString();
    }

    public static boolean coincide(String claveEscrita, String hashGuardado) {
        return cifrar(claveEscrita).equals(hashGuardado);
    }
}
