package com.petsalud.backendpetsalud.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

// Permite que Spring detecte y cree este componente.
@Component
public class EventosSeguridad {

    // Crea el registro asociado a esta clase.
    private static final Logger logger =
            LoggerFactory.getLogger(EventosSeguridad.class);

    // Ejecuta este método cuando se publica un fallo de autenticación.
    @EventListener
    public void registrarFallo(AbstractAuthenticationFailureEvent evento) {
        // Registra el fallo sin incluir contraseñas ni tokens.
        logger.warn("Intento de inicio de sesión fallido. Motivo: {}",
                evento.getException().getClass().getSimpleName());
    }
} 