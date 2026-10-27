package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de la tabla {@code auditoria}.
 * Entrada de la bitácora de auditoría: usuario, operación, tabla afectada y detalle (RF-12).
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "tipo_operacion")
    private String tipoOperacion;

    @Column(name = "tabla_afectada")
    private String tablaAfectada;

    @Column(name = "id_registro_afectado")
    private Long idRegistroAfectado;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "direccion_ip")
    private String direccionIp;

    @Column(name = "realizado_en")
    private LocalDateTime realizadoEn = LocalDateTime.now();

    public Long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Long idAuditoria) { this.idAuditoria = idAuditoria; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public Long getIdRegistroAfectado() { return idRegistroAfectado; }
    public void setIdRegistroAfectado(Long idRegistroAfectado) { this.idRegistroAfectado = idRegistroAfectado; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }

    public LocalDateTime getRealizadoEn() { return realizadoEn; }
    public void setRealizadoEn(LocalDateTime realizadoEn) { this.realizadoEn = realizadoEn; }
}
