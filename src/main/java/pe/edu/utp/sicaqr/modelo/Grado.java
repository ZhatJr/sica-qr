package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;

/**
 * MODELO (MVC) · Tabla grado
 *
 * Grados de cada nivel educativo: inicial, primaria y secundaria.
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

    // ---------------- Getters y setters ----------------

    public Long getIdGrado() { return idGrado; }
    public void setIdGrado(Long idGrado) { this.idGrado = idGrado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivelEducativo() { return nivelEducativo; }
    public void setNivelEducativo(String nivelEducativo) { this.nivelEducativo = nivelEducativo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
