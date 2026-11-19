package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    // Creación de producto con datos completos y verificación de nombre, precio, stock y categoría
    @Test
    void crearProducto() {
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Alimento Premium Perro 15kg")
                .descripcion("Nutrición balanceada para perros adultos")
                .precio(new BigDecimal("35990"))
                .stock(20)
                .categoria("Alimentos")
                .build();

        assertNotNull(producto);
        assertEquals(1L, producto.getId());
        assertNotNull(producto.getNombre());
        assertFalse(producto.getNombre().isBlank());
        assertEquals("Alimento Premium Perro 15kg", producto.getNombre());
        assertEquals("Nutrición balanceada para perros adultos", producto.getDescripcion());
        assertEquals(new BigDecimal("35990"), producto.getPrecio());
        assertEquals(20, producto.getStock());
        assertEquals("Alimentos", producto.getCategoria());
    }

    // Comparación de igualdad entre dos instancias de Producto con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        Producto producto1 = Producto.builder()
                .id(1L)
                .nombre("Alimento Premium Perro 15kg")
                .precio(new BigDecimal("35990"))
                .stock(20)
                .categoria("Alimentos")
                .build();

        Producto producto2 = Producto.builder()
                .id(1L)
                .nombre("Alimento Premium Perro 15kg")
                .precio(new BigDecimal("35990"))
                .stock(20)
                .categoria("Alimentos")
                .build();

        assertEquals(producto1, producto2);
        assertEquals(producto1.hashCode(), producto2.hashCode());
    }
}