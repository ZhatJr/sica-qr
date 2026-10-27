package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Estudiante;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/** DAO · Acceso a la tabla matricula. */
@Repository
public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {

    // Matricula del estudiante en el periodo activo
    Optional<Matricula> findByEstudianteAndPeriodoAndEstado(Estudiante estudiante,
                                                            PeriodoAcademico periodo,
                                                            String estado);

    long countByPeriodoAndEstado(PeriodoAcademico periodo, String estado);
}
