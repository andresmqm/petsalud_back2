package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Notificacion;
import com.petsalud.backendpetsalud.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private NotificacionController notificacionController;

    private Notificacion notificacionValida;

    @BeforeEach
    void setUp() {
        notificacionValida = Notificacion.builder()
                .id(1L)
                .mensaje("Recordatorio")
                .leida(false)
                .build();
    }

    // Envío, consulta por ID (200 y 404), listado por usuario, no leídas, marcar como leída y eliminación
    @Test
    void operacionesNotificacion() {
        when(notificacionService.enviarNotificacion(1L, notificacionValida)).thenReturn(notificacionValida);
        when(notificacionService.obtenerNotificacionPorId(1L)).thenReturn(Optional.of(notificacionValida));
        when(notificacionService.obtenerNotificacionPorId(99L)).thenReturn(Optional.empty());
        when(notificacionService.listarNotificacionesPorUsuario(1L)).thenReturn(List.of(notificacionValida));
        when(notificacionService.listarNotificacionesNoLeidas(1L)).thenReturn(List.of(notificacionValida));

        assertEquals(HttpStatus.OK, notificacionController.enviarNotificacion(1L, notificacionValida).getStatusCode());
        assertEquals(HttpStatus.OK, notificacionController.obtenerNotificacionPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, notificacionController.obtenerNotificacionPorId(99L).getStatusCode());
        assertEquals(1, notificacionController.listarNotificacionesPorUsuario(1L).getBody().size());
        assertEquals(1, notificacionController.listarNotificacionesNoLeidas(1L).getBody().size());

        ResponseEntity<Void> leida = notificacionController.marcarComoLeida(1L);
        assertEquals(HttpStatus.NO_CONTENT, leida.getStatusCode());
        verify(notificacionService, times(1)).marcarComoLeida(1L);

        ResponseEntity<Void> deleted = notificacionController.eliminarNotificacion(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(notificacionService, times(1)).eliminarNotificacion(1L);
    }
}