package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.service.MascotaService;
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
class MascotaControllerTest {

    @Mock
    private MascotaService mascotaService;

    @InjectMocks
    private MascotaController mascotaController;

    private Mascota mascotaValida;

    @BeforeEach
    void setUp() {
        mascotaValida = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .build();
    }

    // Registro de mascota para un propietario y búsqueda por ID (HTTP 200 OK y HTTP 404 Not Found)
    @Test
    void registrarYObtenerMascota() {
        when(mascotaService.registrarMascota(mascotaValida, 1L)).thenReturn(mascotaValida);
        when(mascotaService.obtenerMascotaPorId(10L)).thenReturn(Optional.of(mascotaValida));
        when(mascotaService.obtenerMascotaPorId(99L)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.OK, mascotaController.registrarMascota(1L, mascotaValida).getStatusCode());
        assertEquals(HttpStatus.OK, mascotaController.obtenerMascotaPorId(10L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, mascotaController.obtenerMascotaPorId(99L).getStatusCode());
    }

    // Actualización de datos, listado por propietario y eliminación de mascota (HTTP 204 No Content)
    @Test
    void actualizarListarYEliminarMascota() {
        when(mascotaService.actualizarMascota(10L, mascotaValida)).thenReturn(mascotaValida);
        when(mascotaService.listarMascotasPorPropietario(1L)).thenReturn(List.of(mascotaValida));

        assertEquals(HttpStatus.OK, mascotaController.actualizarMascota(10L, mascotaValida).getStatusCode());
        assertEquals(1, mascotaController.listarMascotasPorPropietario(1L).getBody().size());

        ResponseEntity<Void> deleted = mascotaController.eliminarMascota(10L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(mascotaService, times(1)).eliminarMascota(10L);
    }
}