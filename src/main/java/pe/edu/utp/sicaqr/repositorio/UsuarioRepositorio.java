package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Usuario;

import java.util.Optional;

/**
 * Repositorio de la entidad {@link Usuario}.
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    /** Busca una cuenta por su nombre de usuario. */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
