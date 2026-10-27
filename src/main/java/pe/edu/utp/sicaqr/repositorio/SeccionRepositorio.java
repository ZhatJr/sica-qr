package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Seccion;

import java.util.List;

/**
 * Repositorio de la entidad {@link Seccion}.
 */
@Repository
public interface SeccionRepositorio extends JpaRepository<Seccion, Long> {

    List<Seccion> findByActivoTrueOrderByIdSeccionAsc();
}
