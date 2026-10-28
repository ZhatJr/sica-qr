package pe.edu.utp.sicaqr.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import pe.edu.utp.sicaqr.dto.MensajeError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Configuración de Spring Security (RF-12).
 * Define qué rol puede usar cada ruta de la API, según el mapeo de ventanas
 * y usuarios (TABLA 25). Spring Security verifica el permiso antes de que la
 * petición llegue al controlador; las rutas de la API no listadas se rechazan.
 */
@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    private final ObjectMapper json;

    public ConfiguracionSeguridad(ObjectMapper json) {
        this.json = json;
    }

    @Bean
    public SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        http
                // El inicio y cierre de sesión los maneja AutenticacionControlador
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                // API consumida solo por las pantallas del mismo sitio; la cookie
                // de sesión es SameSite=Strict (application.properties)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(permisos -> permisos
                        // V01: inicio y cierre de sesión
                        .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                        // V02: panel y secciones, cualquier usuario autenticado
                        .requestMatchers("/api/auth/sesion", "/api/panel/**", "/api/secciones").authenticated()
                        // V03: estudiantes y credenciales QR
                        .requestMatchers("/api/estudiantes", "/api/credenciales/**")
                                .hasRole(Roles.ADMINISTRADOR)
                        // V04: registro de ingreso
                        .requestMatchers(HttpMethod.POST, "/api/asistencias/qr", "/api/asistencias/dni")
                                .hasRole(Roles.AUXILIAR)
                        // V05: consulta de asistencia
                        .requestMatchers(HttpMethod.GET, "/api/asistencias")
                                .hasAnyRole(Roles.AUXILIAR, Roles.DOCENTE, Roles.DIRECCION)
                        // V05: reporte en Excel, V07 justificaciones y V08 alertas
                        .requestMatchers("/api/reportes/**", "/api/justificaciones", "/api/alertas")
                                .hasAnyRole(Roles.DOCENTE, Roles.DIRECCION)
                        // V06: notificaciones
                        .requestMatchers("/api/notificaciones")
                                .hasAnyRole(Roles.ADMINISTRADOR, Roles.AUXILIAR)
                        // Cualquier otra ruta de la API queda bloqueada
                        .requestMatchers("/api/**").denyAll()
                        // Pantallas, estilos y scripts: cada pantalla consulta la sesión al cargar
                        .anyRequest().permitAll())

                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((request, response, e) ->
                                responder(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Debe iniciar sesión"))
                        .accessDeniedHandler((request, response, e) ->
                                responder(response, HttpServletResponse.SC_FORBIDDEN,
                                        "Su rol no tiene permiso para esta operación")));

        return http.build();
    }

    /** Respuesta de error con el mismo formato JSON que ManejadorErrores. */
    private void responder(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        json.writeValue(response.getWriter(), new MensajeError(estado, mensaje));
    }
}
