package com.petsalud.backendpetsalud.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar anotaciones como @PreAuthorize("hasRole('GERENTE')")
public class SecurityConfig {

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Endpoints públicos de autenticación y registro
                        .requestMatchers("/api/auth/**", "/api/usuarios/registro", "/api/usuarios/login", "/api/login").permitAll()
                        
                        // Restricciones por roles de la guía (CLIENTE, EMPLEADO, GERENTE)
                        .requestMatchers("/api/usuarios/gerencia/**").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers("/api/usuarios/empleados/**").hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")
                        .requestMatchers("/api/usuarios/clientes/**").hasAnyRole("CLIENTE", "EMPLEADO", "GERENTE", "ADMIN")
                        
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // 3. Configuración de sesiones sin estado
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Servicio de usuarios
                .userDetailsService(usuarioDetailsService)

                // 5. Interceptor JWT antes del filtro estándar de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}