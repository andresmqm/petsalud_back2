package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.dto.RegistroRequest;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.security.UsuarioDetailsService;
import com.petsalud.backendpetsalud.service.UsuarioService;
import com.petsalud.backendpetsalud.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioDetailsService usuarioDetailsService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .apellido("Quintero")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();
    }

    // Registro de usuario y autenticación con generación de token JWT
    @Test
    void registroYLogin() {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setNombre("Andrés");
        registroRequest.setApellido("Quintero");
        registroRequest.setCorreo("andres@petsalud.cl");
        registroRequest.setPassword("Password123!");

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuarioValido);
        ResponseEntity<String> regResponse = usuarioController.registrarUsuario(registroRequest);
        assertEquals(HttpStatus.OK, regResponse.getStatusCode());
        assertEquals("Usuario registrado exitosamente", regResponse.getBody());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("andres@petsalud.cl");
        loginRequest.setPassword("Password123!");

        UserDetails userDetails = new User("andres@petsalud.cl", "Password123!", Collections.emptyList());
        when(usuarioDetailsService.loadUserByUsername("andres@petsalud.cl")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token-123");

        ResponseEntity<?> loginResponse = usuarioController.login(loginRequest);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertInstanceOf(AuthResponse.class, loginResponse.getBody());
    }

    // Verificación de endpoints de acceso por roles (gerencia, empleados y clientes)
    @Test
    void endpointsPorRol() {
        assertEquals(HttpStatus.OK, usuarioController.soloGerencia().getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.soloEmpleados().getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.soloClientes().getStatusCode());
    }

    // Consulta por ID (200 y 404), actualización, listado general y eliminación de usuario
    @Test
    void crudUsuarios() {
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuarioValido));
        when(usuarioService.obtenerUsuarioPorId(99L)).thenReturn(Optional.empty());
        when(usuarioService.actualizarUsuario(1L, usuarioValido)).thenReturn(usuarioValido);
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioValido));

        assertEquals(HttpStatus.OK, usuarioController.obtenerUsuarioPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.obtenerUsuarioPorId(99L).getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.actualizarUsuario(1L, usuarioValido).getStatusCode());
        assertEquals(1, usuarioController.listarUsuarios().getBody().size());

        ResponseEntity<Void> deleted = usuarioController.eliminarUsuario(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}