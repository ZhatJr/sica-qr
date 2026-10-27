package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.PeriodoAcademico;

import java.util.Optional;

/** DAO · Acceso a la tabla periodo_academico. */
@Repository
public interface PeriodoAcademicoRepositorio extends JpaRepository<PeriodoAcademico, Long> {

    // Solo puede haber un periodo ACTIVO (indice uq_periodo_activo)
    Optional<PeriodoAcademico> findByEstado(String estado);
}
