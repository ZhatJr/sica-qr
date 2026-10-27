package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * MODELO (MVC) · Tabla configuracion
 *
 * Reglas de horario del periodo: entrada, tolerancia y corte (RF-02).
 */
@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private PeriodoAcademico periodo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_creador")
    private Usuario usuarioCreador;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "tolerancia_minutos")
    private Integer toleranciaMinutos;

    @Column(name = "hora_corte")
    private LocalTime horaCorte;

    @Column(name = "plazo_justificacion_dias")
    private Integer plazoJustificacionDias;

    @Column(name = "umbral_tardanzas")
    private Integer umbralTardanzas;

    @Column(name = "umbral_inasistencias")
    private Integer umbralInasistencias;

    @Column(name = "vigente_desde")
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    // ---------------- Getters y setters ----------------

    public Long getIdConfiguracion() { return idConfiguracion; }
    public void setIdConfiguracion(Long idConfiguracion) { this.idConfiguracion = idConfiguracion; }

    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }

    public Usuario getUsuarioCreador() { return usuarioCreador; }
    public void setUsuarioCreador(Usuario usuarioCreador) { this.usuarioCreador = usuarioCreador; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public Integer getToleranciaMinutos() { return toleranciaMinutos; }
    public void setToleranciaMinutos(Integer toleranciaMinutos) { this.toleranciaMinutos = toleranciaMinutos; }

    public LocalTime getHoraCorte() { return horaCorte; }
    public void setHoraCorte(LocalTime horaCorte) { this.horaCorte = horaCorte; }

    public Integer getPlazoJustificacionDias() { return plazoJustificacionDias; }
    public void setPlazoJustificacionDias(Integer plazoJustificacionDias) { this.plazoJustificacionDias = plazoJustificacionDias; }

    public Integer getUmbralTardanzas() { return umbralTardanzas; }
    public void setUmbralTardanzas(Integer umbralTardanzas) { this.umbralTardanzas = umbralTardanzas; }

    public Integer getUmbralInasistencias() { return umbralInasistencias; }
    public void setUmbralInasistencias(Integer umbralInasistencias) { this.umbralInasistencias = umbralInasistencias; }

    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }

    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
