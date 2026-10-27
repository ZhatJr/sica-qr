package pe.edu.utp.sicaqr.servicio;

import pe.edu.utp.sicaqr.dto.FilaAsistencia;
import pe.edu.utp.sicaqr.dto.RegistroDniPeticion;
import pe.edu.utp.sicaqr.dto.RegistroQrPeticion;
import pe.edu.utp.sicaqr.dto.ResultadoRegistro;
import pe.edu.utp.sicaqr.dto.ResumenDia;
import pe.edu.utp.sicaqr.dto.SeccionDto;
import pe.edu.utp.sicaqr.dto.UsuarioSesion;

import java.time.LocalDate;
import java.util.List;

/** SOLID · Segregacion de interfaces: solo operaciones de asistencia. */
public interface ServicioAsistencia {

    /** RF-01 y RF-02: registra el ingreso leyendo el QR y lo clasifica. */
    ResultadoRegistro registrarPorQr(RegistroQrPeticion peticion, UsuarioSesion usuario, String ip);

    /** RF-10: registro manual de contingencia por DNI. */
    ResultadoRegistro registrarPorDni(RegistroDniPeticion peticion, UsuarioSesion usuario, String ip);

    /** RF-04: consulta por seccion (opcional) y rango de fechas. */
    List<FilaAsistencia> consultar(Long idSeccion, LocalDate desde, LocalDate hasta);

    ResumenDia resumenDelDia(LocalDate fecha);

    List<SeccionDto> listarSecciones();
}
