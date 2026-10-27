package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.CalendarioEscolar;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositorio de la entidad {@link CalendarioEscolar}.
 */
@Repository
public interface CalendarioEscolarRepositorio extends JpaRepository<CalendarioEscolar, Long> {

    Optional<CalendarioEscolar> findByPeriodoAndFecha(PeriodoAcademico periodo, LocalDate fecha);
}
