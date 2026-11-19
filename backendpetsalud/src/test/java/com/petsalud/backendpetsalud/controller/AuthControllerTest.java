package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.dto.RegistroRequest;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.security.SecurityMonitorService;
import com.petsalud.backendpetsalud.service.UsuarioService;
import com.petsalud.backendpetsalud.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityMonitorService securityMonitorService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    // Registro exitoso de un usuario nuevo
    @Test
    void register() {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setNombre("Andrés");
        registroRequest.setApellido("Quintero");
        registroRequest.setCorreo("andres@petsalud.cl");
        registroRequest.setPassword("Password123!");

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<String> response = authController.register(registroRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario registrado exitosamente", response.getBody());
        verify(usuarioService, times(1)).registrarUsuario(any(Usuario.class));
    }

    // Login exitoso con generación de token JWT y registro en monitor de seguridad
    @Test
    void loginExitoso() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("andres@petsalud.cl");
        loginRequest.setPassword("Password123!");

        UserDetails userDetails = new User("andres@petsalud.cl", "Password123!", Collections.emptyList());

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userDetailsService.loadUserByUsername("andres@petsalud.cl")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token-jwt-valido");

        ResponseEntity<?> response = authController.login(loginRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(AuthResponse.class, response.getBody());
        verify(securityMonitorService, times(1)).registrarLoginExitoso("127.0.0.1");
    }

    // Login fallido por credenciales incorrectas, retorna 401 y registra el fallo
    @Test
    void loginFallido() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("andres@petsalud.cl");
        loginRequest.setPassword("clave_incorrecta");

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        ResponseEntity<?> response = authController.login(loginRequest, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());
        verify(securityMonitorService, times(1)).registrarLoginFallido("andres@petsalud.cl", "127.0.0.1");
    }
}