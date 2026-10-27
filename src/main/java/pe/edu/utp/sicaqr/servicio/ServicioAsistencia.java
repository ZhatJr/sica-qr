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

/** Registro y consulta de asistencia. */
public interface ServicioAsistencia {

    /** Registra un ingreso a partir del código QR y lo clasifica (RF-01, RF-02). */
    ResultadoRegistro registrarPorQr(RegistroQrPeticion peticion, UsuarioSesion usuario, String ip);

    /** Registra un ingreso manual por DNI (RF-10). */
    ResultadoRegistro registrarPorDni(RegistroDniPeticion peticion, UsuarioSesion usuario, String ip);

    /** Registros de un rango de fechas, opcionalmente filtrados por sección (RF-04). */
    List<FilaAsistencia> consultar(Long idSeccion, LocalDate desde, LocalDate hasta);

    /** Indicadores de asistencia de una fecha. */
    ResumenDia resumenDelDia(LocalDate fecha);

    /** Secciones activas. */
    List<SeccionDto> listarSecciones();
}
