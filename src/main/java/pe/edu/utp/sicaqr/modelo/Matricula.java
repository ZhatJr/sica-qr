package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad de la tabla {@code matricula}.
 * Matrícula de un estudiante en una sección y un periodo. La asistencia se asocia a la matrícula.
 */
@Entity
@Table(name = "matricula")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long idMatricula;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "id_seccion")
    private Seccion seccion;

    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private PeriodoAcademico periodo;

    @Column(name = "fecha_matricula")
    private LocalDate fechaMatricula;

    @Column(name = "estado")
    private String estado = "ACTIVA";

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }

    public LocalDate getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDate fechaMatricula) { this.fechaMatricula = fechaMatricula; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
