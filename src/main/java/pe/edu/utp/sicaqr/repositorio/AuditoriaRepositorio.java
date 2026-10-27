package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Auditoria;

/**
 * Repositorio de la entidad {@link Auditoria}.
 */
@Repository
public interface AuditoriaRepositorio extends JpaRepository<Auditoria, Long> {
}
