package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;

/**
 * Entidad de la tabla {@code seccion}.
 * Sección de un grado.
 */
@Entity
@Table(name = "seccion")
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seccion")
    private Long idSeccion;

    @ManyToOne
    @JoinColumn(name = "id_grado")
    private Grado grado;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo = true;

    public Long getIdSeccion() { return idSeccion; }
    public void setIdSeccion(Long idSeccion) { this.idSeccion = idSeccion; }

    public Grado getGrado() { return grado; }
    public void setGrado(Grado grado) { this.grado = grado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
