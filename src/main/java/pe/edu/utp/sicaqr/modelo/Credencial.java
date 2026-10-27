package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de la tabla {@code credencial}.
 * Credencial QR de un estudiante. El token es un UUID aleatorio sin datos personales (RF-11).
 */
@Entity
@Table(name = "credencial")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credencial")
    private Long idCredencial;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "id_usuario_emisor")
    private Usuario usuarioEmisor;

    @ManyToOne
    @JoinColumn(name = "id_usuario_revocador")
    private Usuario usuarioRevocador;

    @Column(name = "token_uuid")
    private UUID tokenUuid = UUID.randomUUID();

    @Column(name = "estado")
    private String estado = "ACTIVA";

    @Column(name = "emitida_en")
    private LocalDateTime emitidaEn = LocalDateTime.now();

    @Column(name = "vence_en")
    private LocalDateTime venceEn;

    @Column(name = "revocada_en")
    private LocalDateTime revocadaEn;

    @Column(name = "motivo_revocacion")
    private String motivoRevocacion;

    public Long getIdCredencial() { return idCredencial; }
    public void setIdCredencial(Long idCredencial) { this.idCredencial = idCredencial; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Usuario getUsuarioEmisor() { return usuarioEmisor; }
    public void setUsuarioEmisor(Usuario usuarioEmisor) { this.usuarioEmisor = usuarioEmisor; }

    public Usuario getUsuarioRevocador() { return usuarioRevocador; }
    public void setUsuarioRevocador(Usuario usuarioRevocador) { this.usuarioRevocador = usuarioRevocador; }

    public UUID getTokenUuid() { return tokenUuid; }
    public void setTokenUuid(UUID tokenUuid) { this.tokenUuid = tokenUuid; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getEmitidaEn() { return emitidaEn; }
    public void setEmitidaEn(LocalDateTime emitidaEn) { this.emitidaEn = emitidaEn; }

    public LocalDateTime getVenceEn() { return venceEn; }
    public void setVenceEn(LocalDateTime venceEn) { this.venceEn = venceEn; }

    public LocalDateTime getRevocadaEn() { return revocadaEn; }
    public void setRevocadaEn(LocalDateTime revocadaEn) { this.revocadaEn = revocadaEn; }

    public String getMotivoRevocacion() { return motivoRevocacion; }
    public void setMotivoRevocacion(String motivoRevocacion) { this.motivoRevocacion = motivoRevocacion; }
}
