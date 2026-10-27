package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Rol;

/**
 * DAO · Acceso a la tabla de Rol.
 * JpaRepository ya trae save, findById, findAll y count.
 */
@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {
}
