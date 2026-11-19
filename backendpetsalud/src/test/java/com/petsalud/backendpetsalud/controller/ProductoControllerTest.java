package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Producto;
import com.petsalud.backendpetsalud.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto productoValido;

    @BeforeEach
    void setUp() {
        productoValido = Producto.builder()
                .id(1L)
                .nombre("Alimento Premium")
                .precio(new BigDecimal("25990"))
                .stock(10)
                .categoria("Alimentos")
                .build();
    }

    // Creación, consulta por ID (HTTP 200 OK y HTTP 404 Not Found), actualización y eliminación de producto
    @Test
    void crudProducto() {
        when(productoService.agregarProducto(productoValido)).thenReturn(productoValido);
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(productoValido));
        when(productoService.obtenerProductoPorId(99L)).thenReturn(Optional.empty());
        when(productoService.actualizarProducto(1L, productoValido)).thenReturn(productoValido);

        assertEquals(HttpStatus.OK, productoController.agregarProducto(productoValido).getStatusCode());
        assertEquals(HttpStatus.OK, productoController.obtenerProductoPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, productoController.obtenerProductoPorId(99L).getStatusCode());
        assertEquals(HttpStatus.OK, productoController.actualizarProducto(1L, productoValido).getStatusCode());

        ResponseEntity<Void> deleted = productoController.eliminarProducto(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(productoService, times(1)).eliminarProducto(1L);
    }

    // Listado general de productos, filtrado por categoría y consulta de stock bajo
    @Test
    void listarProductosConFiltros() {
        when(productoService.listarProductos()).thenReturn(List.of(productoValido));
        when(productoService.listarProductosPorCategoria("Alimentos")).thenReturn(List.of(productoValido));
        when(productoService.listarProductosConStockBajo(15)).thenReturn(List.of(productoValido));

        assertEquals(1, productoController.listarProductos().getBody().size());
        assertEquals(1, productoController.listarProductosPorCategoria("Alimentos").getBody().size());
        assertEquals(1, productoController.listarProductosConStockBajo(15).getBody().size());
    }
}