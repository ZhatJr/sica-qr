package pe.edu.utp.sicaqr.modelo;

import jakarta.persistence.*;

/**
 * Entidad de la tabla {@code apoderado}.
 * Apoderado que recibe las notificaciones de asistencia (RF-09).
 */
@Entity
@Table(name = "apoderado")
public class Apoderado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apoderado")
    private Long idApoderado;

    @Column(name = "dni")
    private String dni;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo")
    private String correo;

    @Column(name = "canal_preferido")
    private String canalPreferido;

    @Column(name = "activo")
    private Boolean activo = true;

    public Long getIdApoderado() { return idApoderado; }
    public void setIdApoderado(Long idApoderado) { this.idApoderado = idApoderado; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCanalPreferido() { return canalPreferido; }
    public void setCanalPreferido(String canalPreferido) { this.canalPreferido = canalPreferido; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
