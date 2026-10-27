package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;

/**
 * Entidad de la tabla {@code grado}.
 * Grado de un nivel educativo: inicial, primaria o secundaria.
 */
@Entity
@Table(name = "grado")
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grado")
    private Long idGrado;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nivel_educativo")
    private String nivelEducativo;

    @Column(name = "activo")
    private Boolean activo = true;

    public Long getIdGrado() { return idGrado; }
    public void setIdGrado(Long idGrado) { this.idGrado = idGrado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivelEducativo() { return nivelEducativo; }
    public void setNivelEducativo(String nivelEducativo) { this.nivelEducativo = nivelEducativo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
