package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de la tabla {@code notificacion}.
 * Notificación enviada al apoderado por una tardanza o inasistencia (RF-09).
 */
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_registro")
    private RegistroAsistencia registro;

    @ManyToOne
    @JoinColumn(name = "id_apoderado")
    private Apoderado apoderado;

    @Column(name = "canal")
    private String canal;

    @Column(name = "destino")
    private String destino;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "estado")
    private String estado = "PENDIENTE";

    @Column(name = "numero_intentos")
    private Integer numeroIntentos = 0;

    @Column(name = "creada_en")
    private LocalDateTime creadaEn = LocalDateTime.now();

    @Column(name = "enviada_en")
    private LocalDateTime enviadaEn;

    @Column(name = "detalle_error")
    private String detalleError;

    public Long getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Long idNotificacion) { this.idNotificacion = idNotificacion; }

    public RegistroAsistencia getRegistro() { return registro; }
    public void setRegistro(RegistroAsistencia registro) { this.registro = registro; }

    public Apoderado getApoderado() { return apoderado; }
    public void setApoderado(Apoderado apoderado) { this.apoderado = apoderado; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getNumeroIntentos() { return numeroIntentos; }
    public void setNumeroIntentos(Integer numeroIntentos) { this.numeroIntentos = numeroIntentos; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public LocalDateTime getEnviadaEn() { return enviadaEn; }
    public void setEnviadaEn(LocalDateTime enviadaEn) { this.enviadaEn = enviadaEn; }

    public String getDetalleError() { return detalleError; }
    public void setDetalleError(String detalleError) { this.detalleError = detalleError; }
}
