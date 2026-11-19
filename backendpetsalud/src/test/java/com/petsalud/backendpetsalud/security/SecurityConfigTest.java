package com.petsalud.backendpetsalud.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    // Verificación de beans PasswordEncoder (BCrypt) y AuthenticationManager
    @Test
    void validarBeansDeSeguridad() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();

        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);

        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(authManager);

        assertEquals(authManager, securityConfig.authenticationManager(authConfig));
    }
}
