package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Matricula;
import pe.edu.utp.sicaqr.modelo.RegistroAsistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de la entidad {@link RegistroAsistencia}.
 */
@Repository
public interface RegistroAsistenciaRepositorio extends JpaRepository<RegistroAsistencia, Long> {

    /** Registro de una matrícula en una fecha (control de duplicados). */
    Optional<RegistroAsistencia> findByMatriculaAndFechaAsistencia(Matricula matricula, LocalDate fecha);

    /** Busca un registro por su identificador de sincronización (idempotencia, RNF-05). */
    Optional<RegistroAsistencia> findByUuidSincronizacion(UUID uuidSincronizacion);

    /** Registros de un rango de fechas (RF-04). */
    List<RegistroAsistencia> findByFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(
            LocalDate desde, LocalDate hasta);

    /** Registros de una sección en un rango de fechas (RF-04). */
    List<RegistroAsistencia> findByMatriculaSeccionIdSeccionAndFechaAsistenciaBetweenOrderByFechaAsistenciaDescCapturadoEnDesc(
            Long idSeccion, LocalDate desde, LocalDate hasta);

    /** Cantidad de registros de una fecha con el estado indicado. */
    long countByFechaAsistenciaAndEstado(LocalDate fecha, String estado);

    /** Cantidad de registros de una fecha. */
    long countByFechaAsistencia(LocalDate fecha);
}
