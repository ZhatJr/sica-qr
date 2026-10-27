package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Configuracion;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/**
 * Repositorio de la entidad {@link Configuracion}.
 */
@Repository
public interface ConfiguracionRepositorio extends JpaRepository<Configuracion, Long> {

    /** Configuración activa del periodo (solo puede haber una). */
    Optional<Configuracion> findByPeriodoAndActivoTrue(PeriodoAcademico periodo);
}
