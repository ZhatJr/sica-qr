# SICA-QR

**Sistema de Control de Asistencia Escolar mediante Código QR**
I.E. N.° 14762 "Luciano Castillo Colonna" · C.P. Miraflores, La Huaca, Paita, Piura

Curso Integrador I: Sistemas – Software · Universidad Tecnológica del Perú, Filial Piura
Docente: Mg. Ing. Richard Doann Asencios Vilchez

---

## 1. Avance actual (tercer avance · 40 %)

| RF | Funcionalidad | Estado |
|---|---|---|
| RF-12 | Inicio de sesión por rol y bitácora de auditoría | Implementado |
| RF-11 | Emisión y revocación de credenciales QR | Implementado |
| RF-01 | Registro de ingreso por código QR | Implementado |
| RF-02 | Clasificación automática: puntual, tardanza o inasistencia | Implementado |
| RF-07 | Reporte de asistencia en Excel | Implementado |
| RF-04 | Consulta de asistencia por sección y fechas | Implementado |
| RF-10 | Registro manual de contingencia por DNI | Implementado |
| RF-05, RF-06, RF-08, RF-09 | Justificaciones, alertas y notificaciones | Solo consulta; lógica en el cuarto avance |
| RF-03 | Registro sin conexión | Parcial: el servidor ya separa la hora de captura de la de registro |

## 2. Tecnologías

| Componente | Tecnología |
|---|---|
| Backend y API REST | Java 21+ · Spring Boot 3.5 |
| Base de datos | PostgreSQL (17 tablas, script del segundo avance) |
| Interfaz | HTML, CSS y JavaScript |
| Códigos QR | ZXing |
| Librerías del curso | Apache POI · Google Guava · Apache Commons Lang · Logback |

## 3. Arquitectura

El sistema aplica el patrón **MVC** sobre una arquitectura en capas:

```
 Pantallas HTML/CSS/JS  (VISTA)
        │   peticiones GET / POST / PUT  (JSON)
        ▼
 controlador/   @RestController  ─ recibe la petición y responde   (CONTROLADOR)
        │
        ▼
 servicio/      reglas del negocio: clasificar, emitir, validar
        │
        ▼
 repositorio/   interfaces DAO (JpaRepository) ─ acceso a datos
        │
        ▼
 modelo/        17 entidades @Entity, una por tabla              (MODELO)
        │
        ▼
 PostgreSQL
```

### Paquetes

| Paquete | Contenido |
|---|---|
| `modelo` | 17 entidades, una por cada tabla, con las mismas columnas |
| `repositorio` | 17 interfaces DAO |
| `servicio` | Interfaces y sus implementaciones con las reglas del negocio |
| `controlador` | API REST |
| `dto` | Objetos que viajan entre la API y las pantallas |
| `seguridad` | Control de acceso por rol y manejo de errores |

### Principios SOLID

| Principio | Dónde |
|---|---|
| **S** · Responsabilidad única | `ServicioAsistenciaImpl` solo maneja asistencia. La regla puntual/tardanza está en `ClasificadorAsistencia`, el cifrado en `UtilClave` y la bitácora en `ServicioAuditoria`. |
| **O** · Abierto/cerrado | `ServicioReporte` lo implementa `ServicioReporteExcel`. Un reporte PDF sería otra clase, sin tocar la de Excel. |
| **I** · Segregación de interfaces | `ServicioAsistencia`, `ServicioCredencial` y `ServicioSeguimiento` están separadas por responsabilidad. |
| **D** · Inversión de dependencias | Los controladores reciben interfaces por el constructor, nunca crean sus dependencias con `new`. |

### Patrón DAO

```java
@Repository
public interface CredencialRepositorio extends JpaRepository<Credencial, Long> {
    // Lo que se usa al escanear: buscar la credencial por el token leído
    Optional<Credencial> findByTokenUuidAndEstado(UUID tokenUuid, String estado);
}
```

El servicio no escribe SQL: le pide los datos a la interfaz y Spring genera la consulta.

## 4. Librerías Java

| Librería | Uso | Archivo |
|---|---|---|
| **Apache POI** | Genera el reporte de asistencia en Excel, con totales y el estado en color | `ServicioReporteExcel.java` |
| **Google Guava** | `Hashing.sha256()` para cifrar contraseñas y `Preconditions` para validar parámetros | `UtilClave.java` |
| **Apache Commons Lang** | `StringUtils` para validar DNI, campos vacíos y motivos | `ServicioAsistenciaImpl.java`, `ServicioAutenticacionImpl.java`, `ServicioCredencialImpl.java` |
| **Logback** | Bitácora de auditoría en `logs/auditoria.log`, un archivo por día | `logback-spring.xml`, `ServicioAuditoria.java` |

## 5. Seguridad

| Riesgo | Medida |
|---|---|
| Robo de contraseñas | Se guardan cifradas con SHA-256 y sal (Guava) |
| Fuerza bruta | La cuenta se bloquea al quinto intento fallido |
| Descubrir qué usuarios existen | Mismo mensaje para usuario inexistente y contraseña incorrecta |
| Acceso a módulos de otro rol | Se verifica el rol en el servidor en cada petición (401 / 403) |
| Exposición de datos del menor | El QR contiene solo un UUID aleatorio, sin nombre ni DNI (Ley N.° 29733) |
| Credencial perdida | Se revoca y deja de funcionar al instante |
| SQL Injection | No se arma SQL con texto: los repositorios usan consultas con parámetros |
| Robo de sesión | Se crea una sesión nueva al iniciar sesión; expira a los 30 minutos |
| Errores que revelan la base de datos | El usuario recibe un mensaje genérico; el detalle solo va al log |
| Falta de trazabilidad | Toda acción importante queda en la tabla `auditoria` y en `logs/auditoria.log` |

## 6. API REST

| Método | Ruta | Rol | RF |
|---|---|---|---|
| POST | `/api/auth/login` | Todos | RF-12 |
| POST | `/api/auth/logout` | Todos | RF-12 |
| GET | `/api/auth/sesion` | Todos | RF-12 |
| GET | `/api/estudiantes` | Administrador | RF-11 |
| POST | `/api/credenciales` | Administrador | RF-11 |
| PUT | `/api/credenciales/{id}/revocar` | Administrador | RF-11 |
| GET | `/api/credenciales/{token}/qr` | Administrador | RF-11 |
| POST | `/api/asistencias/qr` | Auxiliar | RF-01, RF-02 |
| POST | `/api/asistencias/dni` | Auxiliar | RF-10 |
| GET | `/api/asistencias` | Auxiliar, Docente, Dirección | RF-04 |
| GET | `/api/reportes/asistencia` | Docente, Dirección | RF-07 |
| GET | `/api/panel/resumen` | Todos | RF-04 |
| GET | `/api/secciones` | Todos | — |
| GET | `/api/justificaciones` | Docente, Dirección | RF-05 |
| GET | `/api/alertas` | Docente, Dirección | RF-08 |
| GET | `/api/notificaciones` | Administrador, Auxiliar | RF-09 |

No hay rutas DELETE: en SICA-QR nada se borra físicamente. Las bajas se hacen
cambiando el estado (por ejemplo, la credencial pasa a REVOCADA), para conservar
el historial.

## 7. Cómo ejecutarlo

**Requisitos:** Java 21 o superior, PostgreSQL y VS Code con *Extension Pack for Java*.

1. **Base de datos.** Seguir `bd/LEEME_instalacion.txt`: crear la base `sica_qr` y ejecutar
   `01_SICA_QR_estructura.sql` y luego `02_SICA_QR_datos_prueba.sql`.
2. **Contraseña.** En `src/main/resources/application.properties`, escribir la contraseña de
   PostgreSQL en `spring.datasource.password`.
3. **Ejecutar.** Abrir `SicaQrApplication.java` y hacer clic en **Run**. Esperar el mensaje
   `Started SicaQrApplication`.
4. **Probar.** Abrir `pruebas-api.http` (extensión *REST Client*) y enviar las peticiones en orden.

### Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `auxiliar` | `auxiliar123` | Auxiliar de educación |
| `docente` | `docente123` | Docente o tutor |
| `direccion` | `direccion123` | Dirección |

> El registro de ingreso solo funciona en días de clases. Un sábado, domingo o feriado
> el sistema responde que no es día de clases, porque así lo indica el calendario escolar.

## 8. Pruebas unitarias (TDD)

```
mvn test
```

| Prueba | Verifica |
|---|---|
| `ClasificadorAsistenciaTest` | 07:35 puntual, 07:40 puntual, 07:41 tardanza, 09:00 inasistencia, y que un QR leído a las 07:20 y sincronizado a las 09:30 quede **puntual** |
| `UtilClaveTest` | La contraseña no se guarda en texto plano y el hash de Java coincide con el del script SQL |

## 9. Ramas

| Rama | Contenido |
|---|---|
| `main` | Versión integrada |
| `backend` | `modelo`, `repositorio`, `servicio`, `controlador`, base de datos y pruebas |
| `frontend` | Pantallas en `src/main/resources/static` |
