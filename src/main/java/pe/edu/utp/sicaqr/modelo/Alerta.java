package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * MODELO (MVC) · Tabla alerta
 *
 * Aviso interno cuando un estudiante supera el umbral de tardanzas o faltas (RF-08).
 */
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    @ManyToOne
    @JoinColumn(name = "id_matricula")
    private Matricula matricula;

    @ManyToOne
    @JoinColumn(name = "id_usuario_atencion")
    private Usuario usuarioAtencion;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "nivel")
    private String nivel;

    @Column(name = "cantidad_acumulada")
    private Integer cantidadAcumulada;

    @Column(name = "estado")
    private String estado = "PENDIENTE";

    @Column(name = "generada_en")
    private LocalDateTime generadaEn = LocalDateTime.now();

    @Column(name = "atendida_en")
    private LocalDateTime atendidaEn;

    @Column(name = "observacion")
    private String observacion;

    // ---------------- Getters y setters ----------------

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Matricula getMatricula() { return matricula; }
    public void setMatricula(Matricula matricula) { this.matricula = matricula; }

    public Usuario getUsuarioAtencion() { return usuarioAtencion; }
    public void setUsuarioAtencion(Usuario usuarioAtencion) { this.usuarioAtencion = usuarioAtencion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public Integer getCantidadAcumulada() { return cantidadAcumulada; }
    public void setCantidadAcumulada(Integer cantidadAcumulada) { this.cantidadAcumulada = cantidadAcumulada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getGeneradaEn() { return generadaEn; }
    public void setGeneradaEn(LocalDateTime generadaEn) { this.generadaEn = generadaEn; }

    public LocalDateTime getAtendidaEn() { return atendidaEn; }
    public void setAtendidaEn(LocalDateTime atendidaEn) { this.atendidaEn = atendidaEn; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
