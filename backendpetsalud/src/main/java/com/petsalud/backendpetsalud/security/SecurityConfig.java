package com.petsalud.backendpetsalud.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.petsalud.backendpetsalud.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar anotaciones como @PreAuthorize("hasRole('GERENTE')")
public class SecurityConfig {

    // Crea el registro de mensajes de seguridad (log)
    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired //le pide a Spring que inyecte el servicio de usuarios en el proyecto
    private UsuarioDetailsService usuarioDetailsService;

    @Autowired //le pide a Spring que inyecte el filtro de autenticación JWT en el proyecto
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable()) // Deshabilita la proteccion CSRF ya que se usará el JWT en Authorization.
                .authorizeHttpRequests(auth -> auth 

                        // Permite procesar la respuesta del error.
                         .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Endpoints públicos de autenticación y registro
                        .requestMatchers("/api/auth/**", "/api/usuarios/registro", "/api/usuarios/login", "/api/login").permitAll()
                        
                        //Rutas de gerencia
                        .requestMatchers("/api/usuarios/gerencia/**").hasAnyRole("GERENTE", "ADMIN")
                        //Rutas de clientes
                        .requestMatchers("/api/usuarios/clientes", "/api/usuarios/clientes/**").hasRole("CLIENTE")

                        //Operaciones específicas para los tres roles
                        .requestMatchers(HttpMethod.POST, "/api/citas").hasAnyRole("CLIENTE", "EMPLEADO", "GERENTE")

                        //Rutas del personal de la veterinaria (empleados y gerentes)
                        .requestMatchers("/api/mascotas", "/api/mascotas/**",
                                                    "/api/citas/**",
                                                    "/api/usuarios", "/api/usuarios/**",
                                                    "/api/expedientes", "/api/expedientes/**",
                                                    "/api/productos", "/api/productos/**",
                                                    "/api/ventas", "/api/ventas/**",
                                                    "/api/recompensas", "/api/recompensas/**",
                                                    "/api/notificaciones", "/api/notificaciones/**").hasAnyRole("EMPLEADO", "GERENTE")
                        
                        // Rechaza cualquier otra solicitud que no coincida con las anteriores
                        .anyRequest().denyAll()
                )

                // Configuración de sesiones sin estado
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configura el registro y la respuesta de los accesos rechazados.
                .exceptionHandling(exceptions -> exceptions

                        // Atiende solicitudes sin autenticación válida.
                        .authenticationEntryPoint((request, response, exception) -> {
                            // Registra el rechazo y la dirección de conexión.
                            logger.warn(
                                    "Acceso sin autenticación válida. Método: {} Ruta: {} IP: {}",
                                        request.getMethod(),
                                        request.getRequestURI(),
                                        request.getRemoteAddr()
                                );

                            // Mantiene el estado 403 utilizado hasta ahora.
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        })

                        // Atiende usuarios autenticados sin permisos suficientes.
                        .accessDeniedHandler((request, response, exception) -> {
                            // Registra el rechazo y la dirección de conexión.
                            logger.warn(
                                    "Acceso denegado por permisos. IP: {}",
                                    request.getRemoteAddr());

                            // Devuelve el estado HTTP 403.
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        })
                )

                // Utiliza el servicio de usuarios del proyecto.999
                .userDetailsService(usuarioDetailsService)

                // Interceptor JWT antes del filtro estándar de usuario/contraseña
                // Comprueba la validez del token antes de verificar la autorización de la solicitud.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Devuelve la configuración de seguridad construida.
    }

    @Bean // Administra el objeto que devuelve este método para que Spring lo deje disponible para otras clases
    // Crea un codificador de contraseñas para almacenar las contraseñas de forma segura.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Autenticación de usuarios y contraseñas. Se utiliza para validar las credenciales de los usuarios.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}