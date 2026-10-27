package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Notificacion;

import java.util.List;

/** DAO · Acceso a la tabla notificacion. */
@Repository
public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByOrderByCreadaEnDesc();
}
