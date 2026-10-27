package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Auditoria;

/**
 * DAO · Acceso a la tabla de Auditoria.
 * JpaRepository ya trae save, findById, findAll y count.
 */
@Repository
public interface AuditoriaRepositorio extends JpaRepository<Auditoria, Long> {
}
