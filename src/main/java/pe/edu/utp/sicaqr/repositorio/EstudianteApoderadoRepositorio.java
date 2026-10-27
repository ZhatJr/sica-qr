package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.EstudianteApoderado;

/**
 * DAO · Acceso a la tabla de EstudianteApoderado.
 * JpaRepository ya trae save, findById, findAll y count.
 */
@Repository
public interface EstudianteApoderadoRepositorio extends JpaRepository<EstudianteApoderado, Long> {
}
