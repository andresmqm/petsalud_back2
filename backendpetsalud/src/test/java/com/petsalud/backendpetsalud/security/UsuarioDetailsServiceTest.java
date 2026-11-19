package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    // Carga exitosa de UserDetails cuando el correo existe en la base de datos
    @Test
    void loadUserByUsernameExitoso() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("andres@petsalud.cl")
                .password("encodedPass")
                .rol(Rol.EMPLEADO)
                .build();

        when(usuarioRepository.findByEmail("andres@petsalud.cl")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioDetailsService.loadUserByUsername("andres@petsalud.cl");

        assertNotNull(result);
        assertEquals("andres@petsalud.cl", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
    }

    // Lanzamiento de UsernameNotFoundException cuando el correo no está registrado
    @Test
    void loadUserByUsernameNoEncontrado() {
        when(usuarioRepository.findByEmail("noexiste@petsalud.cl")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioDetailsService.loadUserByUsername("noexiste@petsalud.cl");
        });

        assertTrue(ex.getMessage().contains("noexiste@petsalud.cl"));
    }
}