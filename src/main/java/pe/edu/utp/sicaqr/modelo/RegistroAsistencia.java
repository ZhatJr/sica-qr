package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODELO (MVC) · Tabla registro_asistencia
 *
 * Cada ingreso de un estudiante (RF-01, RF-02).
 *
 * capturadoEn  = hora real en que se leyo el QR. Decide si es PUNTUAL o TARDANZA.
 * registradoEn = hora en que el servidor guardo la fila. Puede ser posterior si
 *                el registro se hizo sin conexion.
 */
@Entity
@Table(name = "registro_asistencia")
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    @ManyToOne
    @JoinColumn(name = "id_matricula")
    private Matricula matricula;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registro")
    private Usuario usuarioRegistro;

    @Column(name = "fecha_asistencia")
    private LocalDate fechaAsistencia;

    @Column(name = "capturado_en")
    private LocalDateTime capturadoEn;

    @Column(name = "registrado_en")
    private LocalDateTime registradoEn = LocalDateTime.now();

    @Column(name = "estado")
    private String estado;

    @Column(name = "metodo_registro")
    private String metodoRegistro;

    @Column(name = "uuid_sincronizacion")
    private UUID uuidSincronizacion = UUID.randomUUID();

    @Column(name = "origen_registro")
    private String origenRegistro = "EN_LINEA";

    @Column(name = "estado_sincronizacion")
    private String estadoSincronizacion = "SINCRONIZADO";

    @Column(name = "sincronizado_en")
    private LocalDateTime sincronizadoEn;

    @Column(name = "observacion")
    private String observacion;

    // ---------------- Getters y setters ----------------

    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }

    public Matricula getMatricula() { return matricula; }
    public void setMatricula(Matricula matricula) { this.matricula = matricula; }

    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public LocalDate getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(LocalDate fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }

    public LocalDateTime getCapturadoEn() { return capturadoEn; }
    public void setCapturadoEn(LocalDateTime capturadoEn) { this.capturadoEn = capturadoEn; }

    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(LocalDateTime registradoEn) { this.registradoEn = registradoEn; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoRegistro() { return metodoRegistro; }
    public void setMetodoRegistro(String metodoRegistro) { this.metodoRegistro = metodoRegistro; }

    public UUID getUuidSincronizacion() { return uuidSincronizacion; }
    public void setUuidSincronizacion(UUID uuidSincronizacion) { this.uuidSincronizacion = uuidSincronizacion; }

    public String getOrigenRegistro() { return origenRegistro; }
    public void setOrigenRegistro(String origenRegistro) { this.origenRegistro = origenRegistro; }

    public String getEstadoSincronizacion() { return estadoSincronizacion; }
    public void setEstadoSincronizacion(String estadoSincronizacion) { this.estadoSincronizacion = estadoSincronizacion; }

    public LocalDateTime getSincronizadoEn() { return sincronizadoEn; }
    public void setSincronizadoEn(LocalDateTime sincronizadoEn) { this.sincronizadoEn = sincronizadoEn; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
