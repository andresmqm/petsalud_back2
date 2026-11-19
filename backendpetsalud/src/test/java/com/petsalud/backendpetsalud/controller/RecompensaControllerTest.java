package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Recompensa;
import com.petsalud.backendpetsalud.service.RecompensaService;
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
class RecompensaControllerTest {

    @Mock
    private RecompensaService recompensaService;

    @InjectMocks
    private RecompensaController recompensaController;

    private Recompensa recompensaValida;

    @BeforeEach
    void setUp() {
        recompensaValida = Recompensa.builder()
                .id(1L)
                .puntos(100)
                .descripcion("Bono bienvenida")
                .build();
    }

    // Asignación, búsqueda por ID (200 y 404), listado por usuario y eliminación de recompensa
    @Test
    void operacionesRecompensa() {
        when(recompensaService.agregarRecompensa(1L, recompensaValida)).thenReturn(recompensaValida);
        when(recompensaService.obtenerRecompensaPorId(1L)).thenReturn(Optional.of(recompensaValida));
        when(recompensaService.obtenerRecompensaPorId(99L)).thenReturn(Optional.empty());
        when(recompensaService.listarRecompensasPorUsuario(1L)).thenReturn(List.of(recompensaValida));

        assertEquals(HttpStatus.OK, recompensaController.agregarRecompensa(1L, recompensaValida).getStatusCode());
        assertEquals(HttpStatus.OK, recompensaController.obtenerRecompensaPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, recompensaController.obtenerRecompensaPorId(99L).getStatusCode());
        assertEquals(1, recompensaController.listarRecompensasPorUsuario(1L).getBody().size());

        ResponseEntity<Void> deleted = recompensaController.eliminarRecompensa(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(recompensaService, times(1)).eliminarRecompensa(1L);
    }
}