package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.repository.MascotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaSecurityTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MascotaSecurity mascotaSecurity;

    // Retorna true cuando el usuario está autenticado y es el propietario de la mascota
    @Test
    void esPropietarioValido() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("cliente@petsalud.cl");
        when(mascotaRepository.existsByIdAndPropietarioEmail(10L, "cliente@petsalud.cl")).thenReturn(true);

        assertTrue(mascotaSecurity.esPropietario(10L, authentication));
    }

    // Retorna false cuando authentication es null, no está autenticado o mascotaId es null
    @Test
    void esPropietarioCasosInvalidos() {
        assertFalse(mascotaSecurity.esPropietario(10L, null));

        when(authentication.isAuthenticated()).thenReturn(false);
        assertFalse(mascotaSecurity.esPropietario(10L, authentication));

        when(authentication.isAuthenticated()).thenReturn(true);
        assertFalse(mascotaSecurity.esPropietario(null, authentication));
    }
}