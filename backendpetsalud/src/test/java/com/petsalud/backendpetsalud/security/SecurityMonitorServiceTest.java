package com.petsalud.backendpetsalud.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityMonitorServiceTest {

    // Registro de múltiples intentos fallidos (incluyendo umbral de alerta >= 5) y limpieza tras login exitoso
    @Test
    void registrarIntentosFallidosYExitoso() {
        SecurityMonitorService monitorService = new SecurityMonitorService();
        String ip = "192.168.1.50";
        String email = "usuario@petsalud.cl";

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                monitorService.registrarLoginFallido(email, ip);
            }
            monitorService.registrarLoginExitoso(ip);
        });
    }
}
