package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Apoderado;

/**
 * Repositorio de la entidad {@link Apoderado}.
 */
@Repository
public interface ApoderadoRepositorio extends JpaRepository<Apoderado, Long> {
}
