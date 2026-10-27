package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * MODELO (MVC) · Tabla calendario_escolar
 *
 * Dias laborables y no laborables del periodo. Evita que un feriado cuente como falta.
 */
@Entity
@Table(name = "calendario_escolar")
public class CalendarioEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calendario")
    private Long idCalendario;

    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private PeriodoAcademico periodo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "es_laborable")
    private Boolean esLaborable = true;

    @Column(name = "descripcion")
    private String descripcion;

    // ---------------- Getters y setters ----------------

    public Long getIdCalendario() { return idCalendario; }
    public void setIdCalendario(Long idCalendario) { this.idCalendario = idCalendario; }

    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Boolean getEsLaborable() { return esLaborable; }
    public void setEsLaborable(Boolean esLaborable) { this.esLaborable = esLaborable; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
