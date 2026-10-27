package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de la tabla {@code justificacion}.
 * Justificación de una inasistencia o tardanza (RF-05, RF-06).
 */
@Entity
@Table(name = "justificacion")
public class Justificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_justificacion")
    private Long idJustificacion;

    @ManyToOne
    @JoinColumn(name = "id_registro")
    private RegistroAsistencia registro;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registro")
    private Usuario usuarioRegistro;

    @ManyToOne
    @JoinColumn(name = "id_usuario_revisor")
    private Usuario usuarioRevisor;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "documento_url")
    private String documentoUrl;

    @Column(name = "estado")
    private String estado = "PENDIENTE";

    @Column(name = "presentada_en")
    private LocalDateTime presentadaEn = LocalDateTime.now();

    @Column(name = "revisada_en")
    private LocalDateTime revisadaEn;

    @Column(name = "comentario_revision")
    private String comentarioRevision;

    public Long getIdJustificacion() { return idJustificacion; }
    public void setIdJustificacion(Long idJustificacion) { this.idJustificacion = idJustificacion; }

    public RegistroAsistencia getRegistro() { return registro; }
    public void setRegistro(RegistroAsistencia registro) { this.registro = registro; }

    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public Usuario getUsuarioRevisor() { return usuarioRevisor; }
    public void setUsuarioRevisor(Usuario usuarioRevisor) { this.usuarioRevisor = usuarioRevisor; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getDocumentoUrl() { return documentoUrl; }
    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getPresentadaEn() { return presentadaEn; }
    public void setPresentadaEn(LocalDateTime presentadaEn) { this.presentadaEn = presentadaEn; }

    public LocalDateTime getRevisadaEn() { return revisadaEn; }
    public void setRevisadaEn(LocalDateTime revisadaEn) { this.revisadaEn = revisadaEn; }

    public String getComentarioRevision() { return comentarioRevision; }
    public void setComentarioRevision(String comentarioRevision) { this.comentarioRevision = comentarioRevision; }
}
