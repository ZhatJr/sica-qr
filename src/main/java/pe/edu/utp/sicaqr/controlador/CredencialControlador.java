package pe.edu.utp.sicaqr.controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sicaqr.dto.EmitirCredencialPeticion;
import pe.edu.utp.sicaqr.dto.EstudianteCredencialDto;
import pe.edu.utp.sicaqr.dto.RevocarCredencialPeticion;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;
import pe.edu.utp.sicaqr.seguridad.ControlAcceso;
import pe.edu.utp.sicaqr.seguridad.Roles;
import pe.edu.utp.sicaqr.servicio.ServicioCredencial;

import java.util.List;

/** API de estudiantes y credenciales QR (V03, RF-11). Acceso: ADMINISTRADOR. */
@RestController
@RequestMapping("/api")
public class CredencialControlador {

    private final ServicioCredencial servicioCredencial;

    public CredencialControlador(ServicioCredencial servicioCredencial) {
        this.servicioCredencial = servicioCredencial;
    }

    /** GET /api/estudiantes */
    @GetMapping("/estudiantes")
    public List<EstudianteCredencialDto> listar(HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.ADMINISTRADOR);
        return servicioCredencial.listarEstudiantes();
    }

    /** POST /api/credenciales  { "idEstudiante": 4 } */
    @PostMapping("/credenciales")
    public ResponseEntity<EstudianteCredencialDto> emitir(@RequestBody EmitirCredencialPeticion peticion,
                                                          HttpSession sesion, HttpServletRequest request) {
        UsuarioSesion usuario = ControlAcceso.exigirRol(sesion, Roles.ADMINISTRADOR);
        EstudianteCredencialDto emitida =
                servicioCredencial.emitir(peticion.idEstudiante(), usuario, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(emitida);
    }

    /**
     * PUT /api/credenciales/{id}/revocar  { "motivo": "..." }
     * Cambia el estado de la credencial a REVOCADA; no la elimina.
     */
    @PutMapping("/credenciales/{id}/revocar")
    public ResponseEntity<Void> revocar(@PathVariable Long id, @RequestBody RevocarCredencialPeticion peticion,
                                        HttpSession sesion, HttpServletRequest request) {
        UsuarioSesion usuario = ControlAcceso.exigirRol(sesion, Roles.ADMINISTRADOR);
        servicioCredencial.revocar(id, peticion.motivo(), usuario, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    /** GET /api/credenciales/{token}/qr: imagen PNG del código QR. */
    @GetMapping("/credenciales/{token}/qr")
    public ResponseEntity<byte[]> imagenQr(@PathVariable String token, HttpSession sesion) {
        ControlAcceso.exigirRol(sesion, Roles.ADMINISTRADOR);
        byte[] imagen = servicioCredencial.generarImagenQr(token);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imagen);
    }
}
