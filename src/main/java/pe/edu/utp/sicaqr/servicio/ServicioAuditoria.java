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
 * Bitácora de auditoría (RF-12).
 * Cada operación se guarda en la tabla {@code auditoria} y en el archivo
 * {@code logs/auditoria.log} mediante Logback.
 */
@Service
public class ServicioAuditoria {

    /** Logger configurado en logback-spring.xml con salida a logs/auditoria.log. */
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
     * Registra una operación en la bitácora.
     *
     * @param idUsuario  usuario que realiza la operación
     * @param operacion  LOGIN, LOGOUT, INSERT, UPDATE, DELETE o EXPORT
     * @param tabla      tabla afectada
     * @param idAfectado identificador del registro afectado, si corresponde
     * @param detalle    datos de la operación, almacenados como JSON
     * @param ip         dirección IP de origen
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
