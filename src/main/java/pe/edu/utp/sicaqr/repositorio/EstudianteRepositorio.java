package pe.edu.utp.sicaqr.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.sicaqr.modelo.Estudiante;

import java.util.List;
import java.util.Optional;

/** DAO · Acceso a la tabla estudiante. */
@Repository
public interface EstudianteRepositorio extends JpaRepository<Estudiante, Long> {

    // Registro manual de contingencia por DNI (RF-10)
    Optional<Estudiante> findByDni(String dni);

    List<Estudiante> findByActivoTrueOrderByApellidosAscNombresAsc();
}
