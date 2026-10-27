package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Estudiante;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/**
 * Repositorio de la entidad {@link Matricula}.
 */
@Repository
public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {

    /** Matrícula de un estudiante en un periodo, filtrada por estado. */
    Optional<Matricula> findByEstudianteAndPeriodoAndEstado(Estudiante estudiante,
                                                            PeriodoAcademico periodo,
                                                            String estado);

    long countByPeriodoAndEstado(PeriodoAcademico periodo, String estado);
}
