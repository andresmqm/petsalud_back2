package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionTest {

    // Creación de notificación con datos completos y verificación de relación con usuario
    @Test
    void crearNotificacion() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 15, 0);

        Notificacion notificacion = Notificacion.builder()
                .id(1L)
                .mensaje("Recordatorio de vacuna anual para Max")
                .tipo("Recordatorio")
                .fecha(fecha)
                .leida(false)
                .usuario(usuario)
                .build();

        assertNotNull(notificacion);
        assertEquals(1L, notificacion.getId());
        assertEquals("Recordatorio de vacuna anual para Max", notificacion.getMensaje());
        assertEquals("Recordatorio", notificacion.getTipo());
        assertEquals(fecha, notificacion.getFecha());
        assertFalse(notificacion.getLeida());
        assertEquals(usuario, notificacion.getUsuario());
    }

    // Comparación de igualdad entre dos instancias de Notificacion con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 15, 0);

        Notificacion notif1 = Notificacion.builder()
                .id(1L)
                .mensaje("Recordatorio de vacuna")
                .tipo("Recordatorio")
                .fecha(fecha)
                .leida(false)
                .build();

        Notificacion notif2 = Notificacion.builder()
                .id(1L)
                .mensaje("Recordatorio de vacuna")
                .tipo("Recordatorio")
                .fecha(fecha)
                .leida(false)
                .build();

        assertEquals(notif1, notif2);
        assertEquals(notif1.hashCode(), notif2.hashCode());
    }
}