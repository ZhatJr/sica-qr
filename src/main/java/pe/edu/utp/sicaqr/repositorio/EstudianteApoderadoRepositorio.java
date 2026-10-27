package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.EstudianteApoderado;

/**
 * Repositorio de la entidad {@link EstudianteApoderado}.
 */
@Repository
public interface EstudianteApoderadoRepositorio extends JpaRepository<EstudianteApoderado, Long> {
}
