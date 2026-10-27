package pe.edu.utp.sicaqr.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** TDD · Pruebas del cifrado de contrasenas con Google Guava (RNF-04). */
class UtilClaveTest {

    @Test
    @DisplayName("La contrasena no se guarda en texto plano")
    void elHashNoEsLaClave() {
        String hash = UtilClave.cifrar("admin123");
        assertNotEquals("admin123", hash);
        assertEquals(64, hash.length(), "SHA-256 produce 64 caracteres hexadecimales");
    }

    @Test
    @DisplayName("El hash coincide con el guardado en el script de datos de prueba")
    void coincideConLaBaseDeDatos() {
        // Valor de la tabla usuario para 'admin' en 02_SICA_QR_datos_prueba.sql
        assertEquals("374ca1c6dcf652ef60b85fd5266c6a578c471362b27e8f2b978f73c786c1587d",
                UtilClave.cifrar("admin123"));
    }

    @Test
    @DisplayName("Acepta la clave correcta y rechaza una incorrecta")
    void verificaEnAmbosSentidos() {
        String hash = UtilClave.cifrar("auxiliar123");
        assertTrue(UtilClave.coincide("auxiliar123", hash));
        assertFalse(UtilClave.coincide("auxiliar124", hash));
    }
}
