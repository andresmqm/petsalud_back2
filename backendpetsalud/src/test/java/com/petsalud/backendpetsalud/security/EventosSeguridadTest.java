package com.petsalud.backendpetsalud.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventosSeguridadTest {

    // Registro en log ante un evento de fallo de autenticación sin lanzar excepciones
    @Test
    void registrarFalloAutenticacion() {
        EventosSeguridad eventosSeguridad = new EventosSeguridad();
        AuthenticationFailureBadCredentialsEvent evento = new AuthenticationFailureBadCredentialsEvent(
                new UsernamePasswordAuthenticationToken("user", "wrong"),
                new BadCredentialsException("Credenciales incorrectas")
        );

        assertDoesNotThrow(() -> eventosSeguridad.registrarFallo(evento));
    }
}