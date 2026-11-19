package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Cita;
import com.petsalud.backendpetsalud.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private Cita citaValida;

    @BeforeEach
    void setUp() {
        citaValida = Cita.builder()
                .id(1L)
                .motivo("Control general")
                .estado("PROGRAMADA")
                .build();
    }

    // Agendamiento de cita médica parseando fecha ISO 8601 y retornando HTTP 200 OK
    @Test
    void agendarCita() {
        when(citaService.agendarCita(eq(10L), eq(2L), any(LocalDateTime.class), eq("Control general")))
                .thenReturn(citaValida);

        ResponseEntity<Cita> response = citaController.agendarCita(10L, 2L, "2026-12-01T10:00:00", "Control general");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(citaValida, response.getBody());
    }

    // Búsqueda de cita por ID retornando HTTP 200 OK si existe y HTTP 404 Not Found si no existe
    @Test
    void obtenerCitaPorId() {
        when(citaService.obtenerCitaPorId(1L)).thenReturn(Optional.of(citaValida));
        when(citaService.obtenerCitaPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Cita> ok = citaController.obtenerCitaPorId(1L);
        ResponseEntity<Cita> notFound = citaController.obtenerCitaPorId(99L);

        assertEquals(HttpStatus.OK, ok.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode());
    }

    // Actualización de estado de cita y cancelación retornando HTTP 204 No Content
    @Test
    void actualizarEstadoYCancelarCita() {
        citaValida.setEstado("CONFIRMADA");
        when(citaService.actualizarEstadoCita(1L, "CONFIRMADA")).thenReturn(citaValida);

        ResponseEntity<Cita> updated = citaController.actualizarEstadoCita(1L, "CONFIRMADA");
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals("CONFIRMADA", updated.getBody().getEstado());

        ResponseEntity<Void> deleted = citaController.cancelarCita(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(citaService, times(1)).cancelarCita(1L);
    }

    // Listado de citas filtradas por mascota, por veterinario y por rango de fechas
    @Test
    void listarCitas() {
        when(citaService.listarCitasPorMascota(10L)).thenReturn(List.of(citaValida));
        when(citaService.listarCitasPorVeterinario(2L)).thenReturn(List.of(citaValida));
        when(citaService.listarCitasEntreFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(citaValida));

        assertEquals(1, citaController.listarCitasPorMascota(10L).getBody().size());
        assertEquals(1, citaController.listarCitasPorVeterinario(2L).getBody().size());
        assertEquals(1, citaController.listarCitasEntreFechas("2026-12-01T08:00:00", "2026-12-01T18:00:00").getBody().size());
    }
}