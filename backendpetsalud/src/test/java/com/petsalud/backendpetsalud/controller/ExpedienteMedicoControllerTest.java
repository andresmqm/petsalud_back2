package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.ExpedienteMedico;
import com.petsalud.backendpetsalud.service.ExpedienteMedicoService;
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
class ExpedienteMedicoControllerTest {

    @Mock
    private ExpedienteMedicoService expedienteMedicoService;

    @InjectMocks
    private ExpedienteMedicoController expedienteMedicoController;

    private ExpedienteMedico registroValido;

    @BeforeEach
    void setUp() {
        registroValido = ExpedienteMedico.builder()
                .id(1L)
                .diagnostico("Paciente sano")
                .build();
    }

    // Creación, consulta por ID (existente e inexistente), listado por mascota y eliminación de expediente médico
    @Test
    void operacionesExpedienteMedico() {
        when(expedienteMedicoService.agregarRegistro(10L, 2L, registroValido)).thenReturn(registroValido);
        when(expedienteMedicoService.obtenerRegistroPorId(1L)).thenReturn(Optional.of(registroValido));
        when(expedienteMedicoService.obtenerRegistroPorId(99L)).thenReturn(Optional.empty());
        when(expedienteMedicoService.listarRegistrosPorMascota(10L)).thenReturn(List.of(registroValido));

        assertEquals(HttpStatus.OK, expedienteMedicoController.agregarRegistro(10L, 2L, registroValido).getStatusCode());
        assertEquals(HttpStatus.OK, expedienteMedicoController.obtenerRegistroPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, expedienteMedicoController.obtenerRegistroPorId(99L).getStatusCode());
        assertEquals(1, expedienteMedicoController.listarRegistrosPorMascota(10L).getBody().size());

        ResponseEntity<Void> deleted = expedienteMedicoController.eliminarRegistro(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(expedienteMedicoService, times(1)).eliminarRegistro(1L);
    }
}