package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Credencial;
import pe.edu.utp.sicaqr.modelo.Estudiante;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de la entidad {@link Credencial}.
 */
@Repository
public interface CredencialRepositorio extends JpaRepository<Credencial, Long> {

    /** Busca una credencial por su token y estado (lectura del QR, RF-01). */
    Optional<Credencial> findByTokenUuidAndEstado(UUID tokenUuid, String estado);

    /** Credencial de un estudiante en el estado indicado. */
    Optional<Credencial> findByEstudianteAndEstado(Estudiante estudiante, String estado);
}
