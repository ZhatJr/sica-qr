package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;

/**
 * MODELO (MVC) · Tabla estudiante_apoderado
 *
 * Tabla intermedia: un estudiante puede tener varios apoderados y un apoderado varios hijos.
 */
@Entity
@Table(name = "estudiante_apoderado")
public class EstudianteApoderado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante_apoderado")
    private Long idEstudianteApoderado;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "id_apoderado")
    private Apoderado apoderado;

    @Column(name = "parentesco")
    private String parentesco;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @Column(name = "recibe_notificaciones")
    private Boolean recibeNotificaciones = true;

    @Column(name = "activo")
    private Boolean activo = true;

    // ---------------- Getters y setters ----------------

    public Long getIdEstudianteApoderado() { return idEstudianteApoderado; }
    public void setIdEstudianteApoderado(Long idEstudianteApoderado) { this.idEstudianteApoderado = idEstudianteApoderado; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Apoderado getApoderado() { return apoderado; }
    public void setApoderado(Apoderado apoderado) { this.apoderado = apoderado; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    public Boolean getRecibeNotificaciones() { return recibeNotificaciones; }
    public void setRecibeNotificaciones(Boolean recibeNotificaciones) { this.recibeNotificaciones = recibeNotificaciones; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
