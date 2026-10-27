package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Usuario;

import java.util.Optional;

/** DAO · Acceso a la tabla usuario. */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // Spring genera la consulta a partir del nombre del metodo:
    // SELECT * FROM usuario WHERE nombre_usuario = ?
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
