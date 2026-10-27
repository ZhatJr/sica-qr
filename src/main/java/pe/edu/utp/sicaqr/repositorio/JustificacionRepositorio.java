package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Justificacion;

import java.util.List;

/**
 * Repositorio de la entidad {@link Justificacion}.
 */
@Repository
public interface JustificacionRepositorio extends JpaRepository<Justificacion, Long> {

    List<Justificacion> findAllByOrderByPresentadaEnDesc();
}
