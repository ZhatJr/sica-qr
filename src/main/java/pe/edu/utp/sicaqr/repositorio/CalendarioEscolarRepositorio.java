package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.CalendarioEscolar;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.time.LocalDate;
import java.util.Optional;

/** DAO · Acceso a la tabla calendario_escolar. */
@Repository
public interface CalendarioEscolarRepositorio extends JpaRepository<CalendarioEscolar, Long> {

    Optional<CalendarioEscolar> findByPeriodoAndFecha(PeriodoAcademico periodo, LocalDate fecha);
}
