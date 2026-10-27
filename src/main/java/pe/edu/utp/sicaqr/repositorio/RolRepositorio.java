package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Rol;

/**
 * Repositorio de la entidad {@link Rol}.
 */
@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {
}
