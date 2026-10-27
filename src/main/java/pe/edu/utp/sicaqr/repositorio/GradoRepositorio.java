package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Grado;

/**
 * Repositorio de la entidad {@link Grado}.
 */
@Repository
public interface GradoRepositorio extends JpaRepository<Grado, Long> {
}
