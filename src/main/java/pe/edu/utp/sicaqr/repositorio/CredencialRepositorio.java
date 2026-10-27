package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Credencial;
import pe.edu.utp.sicaqr.modelo.Estudiante;

import java.util.Optional;
import java.util.UUID;

/**
 * DAO · Acceso a la tabla credencial.
 *
 * El servicio no escribe SQL: le pide los datos a esta interfaz.
 * Si manana cambia el motor de base de datos, solo cambia esta capa.
 */
@Repository
public interface CredencialRepositorio extends JpaRepository<Credencial, Long> {

    // Lo que se usa al escanear: buscar la credencial por el token leido (RF-01)
    Optional<Credencial> findByTokenUuidAndEstado(UUID tokenUuid, String estado);

    // Credencial vigente de un estudiante (solo puede haber una)
    Optional<Credencial> findByEstudianteAndEstado(Estudiante estudiante, String estado);
}
