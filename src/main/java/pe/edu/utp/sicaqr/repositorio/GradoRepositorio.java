package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Grado;

/**
 * DAO · Acceso a la tabla de Grado.
 * JpaRepository ya trae save, findById, findAll y count.
 */
@Repository
public interface GradoRepositorio extends JpaRepository<Grado, Long> {
}
