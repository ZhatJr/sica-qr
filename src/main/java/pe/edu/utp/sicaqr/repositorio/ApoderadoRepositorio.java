package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Apoderado;

/**
 * DAO · Acceso a la tabla de Apoderado.
 * JpaRepository ya trae save, findById, findAll y count.
 */
@Repository
public interface ApoderadoRepositorio extends JpaRepository<Apoderado, Long> {
}
