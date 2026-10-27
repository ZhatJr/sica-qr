package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Alerta;

import java.util.List;

/** DAO · Acceso a la tabla alerta. */
@Repository
public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByOrderByGeneradaEnDesc();
}
