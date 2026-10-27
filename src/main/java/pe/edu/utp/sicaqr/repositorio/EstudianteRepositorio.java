package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Estudiante;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la entidad {@link Estudiante}.
 */
@Repository
public interface EstudianteRepositorio extends JpaRepository<Estudiante, Long> {

    /** Busca un estudiante por su DNI (registro de contingencia, RF-10). */
    Optional<Estudiante> findByDni(String dni);

    List<Estudiante> findByActivoTrueOrderByApellidosAscNombresAsc();
}
