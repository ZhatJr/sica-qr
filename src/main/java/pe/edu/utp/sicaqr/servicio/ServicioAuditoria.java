package pe.edu.utp.sicaqr.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sicaqr.modelo.Auditoria;
import pe.edu.utp.sicaqr.repositorio.AuditoriaRepositorio;
import pe.edu.utp.sicaqr.repositorio.UsuarioRepositorio;

import java.util.Map;

/**
 * BITACORA DE AUDITORIA (RF-12) · LIBRERIA LOGBACK
 *
 * Cada accion importante se guarda en dos lugares:
 *   1. La tabla auditoria de la base de datos (para consultarla).
 *   2. El archivo logs/auditoria.log, escrito por Logback (respaldo en texto).
 *
 * SRP: esta clase solo registra; no decide nada del negocio.
 */
@Service
public class ServicioAuditoria {

    // Logback: logger con nombre propio, configurado en logback-spring.xml
    private static final Logger bitacora = LoggerFactory.getLogger("AUDITORIA");

    private final AuditoriaRepositorio auditoriaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ObjectMapper json;

    public ServicioAuditoria(AuditoriaRepositorio auditoriaRepositorio,
                             UsuarioRepositorio usuarioRepositorio,
                             ObjectMapper json) {
        this.auditoriaRepositorio = auditoriaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.json = json;
    }

    /**
     * @param operacion LOGIN, LOGOUT, INSERT, UPDATE, DELETE o EXPORT
     * @param detalle   datos del cambio; se guarda como JSON en la columna JSONB
     */
    @Transactional
    public void registrar(Long idUsuario, String operacion, String tabla,
                          Long idAfectado, Map<String, Object> detalle, String ip) {

        String detalleJson = convertirAJson(detalle);

        Auditoria registro = new Auditoria();
        registro.setUsuario(usuarioRepositorio.getReferenceById(idUsuario));
        registro.setTipoOperacion(operacion);
        registro.setTablaAfectada(tabla);
        registro.setIdRegistroAfectado(idAfectado);
        registro.setDetalle(detalleJson);
        registro.setDireccionIp(ip);
        auditoriaRepositorio.save(registro);

        bitacora.info("usuario={} | {} | {} | id={} | {} | ip={}",
                idUsuario, operacion, tabla, idAfectado, detalleJson, ip);
    }

    private String convertirAJson(Map<String, Object> detalle) {
        try {
            return json.writeValueAsString(detalle);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
