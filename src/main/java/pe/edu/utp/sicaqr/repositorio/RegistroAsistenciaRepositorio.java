package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.RegistroAsistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** DAO · Acceso a la tabla registro_asistencia. */
@Repository
public interface RegistroAsistenciaRepositorio extends JpaRepository<RegistroAsistencia, Long> {

    // Control de duplicados: un registro por matricula por dia
    Optional<RegistroAsistencia> findByMatriculaAndFechaAsistencia(Matricula matricula, LocalDate fecha);

    // Idempotencia: si el mismo registro llega dos veces, no se duplica (RNF-05)
    Optional<RegistroAsistencia> findByUuidSincronizacion(UUID uuidSincronizacion);

    // Consulta de asistencia por rango de fechas (RF-04)
    List<RegistroAsistencia> findByFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(
            LocalDate desde, LocalDate hasta);

    // Consulta de asistencia por seccion y rango de fechas (RF-04)
    List<RegistroAsistencia> findByMatriculaSeccionIdSeccionAndFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(
            Long idSeccion, LocalDate desde, LocalDate hasta);

    // Indicadores del panel
    long countByFechaAsistenciaAndEstado(LocalDate fecha, String estado);

    long countByFechaAsistencia(LocalDate fecha);
}
