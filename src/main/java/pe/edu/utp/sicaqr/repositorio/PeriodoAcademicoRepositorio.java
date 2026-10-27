package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/**
 * Repositorio de la entidad {@link PeriodoAcademico}.
 */
@Repository
public interface PeriodoAcademicoRepositorio extends JpaRepository<PeriodoAcademico, Long> {

    /** Devuelve el periodo con el estado indicado (solo puede haber uno ACTIVO). */
    Optional<PeriodoAcademico> findByEstado(String estado);
}
