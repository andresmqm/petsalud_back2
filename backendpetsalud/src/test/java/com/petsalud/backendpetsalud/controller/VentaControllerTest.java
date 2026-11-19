package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Venta;
import com.petsalud.backendpetsalud.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private Venta ventaValida;

    @BeforeEach
    void setUp() {
        ventaValida = Venta.builder()
                .id(1L)
                .total(new BigDecimal("25990"))
                .build();
    }

    // Registro de venta, búsqueda por ID (200 y 404), listado por usuario y listado general
    @Test
    void operacionesVenta() {
        List<Long> productosIds = List.of(1L, 2L);

        when(ventaService.realizarVenta(1L, productosIds)).thenReturn(ventaValida);
        when(ventaService.obtenerVentaPorId(1L)).thenReturn(Optional.of(ventaValida));
        when(ventaService.obtenerVentaPorId(99L)).thenReturn(Optional.empty());
        when(ventaService.listarVentasPorUsuario(1L)).thenReturn(List.of(ventaValida));
        when(ventaService.listarTodasLasVentas()).thenReturn(List.of(ventaValida));

        assertEquals(HttpStatus.OK, ventaController.realizarVenta(1L, productosIds).getStatusCode());
        assertEquals(HttpStatus.OK, ventaController.obtenerVentaPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ventaController.obtenerVentaPorId(99L).getStatusCode());
        assertEquals(1, ventaController.listarVentasPorUsuario(1L).getBody().size());
        assertEquals(1, ventaController.listarTodasLasVentas().getBody().size());
    }
}