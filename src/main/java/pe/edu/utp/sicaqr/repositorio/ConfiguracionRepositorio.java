package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Configuracion;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/** DAO · Acceso a la tabla configuracion. */
@Repository
public interface ConfiguracionRepositorio extends JpaRepository<Configuracion, Long> {

    // Una sola configuracion activa por periodo (indice uq_configuracion_activa_periodo)
    Optional<Configuracion> findByPeriodoAndActivoTrue(PeriodoAcademico periodo);
}
