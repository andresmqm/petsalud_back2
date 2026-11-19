package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Producto;
import com.petsalud.backendpetsalud.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoValido;

    @BeforeEach
    void setUp() {
        productoValido = Producto.builder()
                .id(1L)
                .nombre("Alimento Premium Perro 15kg")
                .descripcion("Nutrición balanceada para perros adultos")
                .precio(new BigDecimal("35990"))
                .stock(20)
                .categoria("Alimentos")
                .build();
    }

    // Con datos válidos y etiquetas HTML, debería sanitizar el texto con Jsoup y guardar el producto
    @Test
    void agregarProducto() {
        Producto productoConHtml = Producto.builder()
                .nombre("<b>Alimento Premium Perro 15kg</b>")
                .descripcion("<script>alert('xss')</script>Nutrición balanceada")
                .precio(new BigDecimal("35990"))
                .stock(20)
                .categoria("<i>Alimentos</i>")
                .build();

        when(productoRepository.findByNombre("Alimento Premium Perro 15kg")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto resultado = productoService.agregarProducto(productoConHtml);

        assertNotNull(resultado);
        assertEquals("Alimento Premium Perro 15kg", resultado.getNombre());
        assertEquals("Nutrición balanceada", resultado.getDescripcion());
        assertEquals("Alimentos", resultado.getCategoria());
        verify(productoRepository, times(1)).save(productoConHtml);
    }

    // Con stock negativo al agregar, debería lanzar ResponseStatusException BAD_REQUEST
    @Test
    void agregarProductoStockNegativo() {
        productoValido.setStock(-5);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            productoService.agregarProducto(productoValido);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("El stock no puede ser negativo", ex.getReason());
        verify(productoRepository, never()).save(any());
    }

    // Con nombre vacío o nulo al agregar, debería lanzar ResponseStatusException BAD_REQUEST
    @Test
    void agregarProductoNombreVacio() {
        productoValido.setNombre("   ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            productoService.agregarProducto(productoValido);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("El nombre del producto no puede estar vacío", ex.getReason());
        verify(productoRepository, never()).save(any());
    }

    // Con un nombre de producto ya registrado, debería lanzar ResponseStatusException BAD_REQUEST
    @Test
    void agregarProductoDuplicado() {
        when(productoRepository.findByNombre("Alimento Premium Perro 15kg")).thenReturn(Optional.of(productoValido));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            productoService.agregarProducto(productoValido);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("El producto ya existe", ex.getReason());
        verify(productoRepository, never()).save(any());
    }

    // Con ID existente y datos válidos, debería actualizar todos los campos y guardar
    @Test
    void actualizarProducto() {
        Producto datosNuevos = Producto.builder()
                .nombre("Alimento Súper Premium")
                .descripcion("Nueva fórmula")
                .precio(new BigDecimal("39990"))
                .stock(15)
                .categoria("Alimentos")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoValido));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto actualizado = productoService.actualizarProducto(1L, datosNuevos);

        assertEquals("Alimento Súper Premium", actualizado.getNombre());
        assertEquals(new BigDecimal("39990"), actualizado.getPrecio());
        assertEquals(15, actualizado.getStock());
        verify(productoRepository, times(1)).save(productoValido);
    }

    // Validación de errores al actualizar: stock negativo, nombre vacío o producto no encontrado
    @Test
    void actualizarProductoValidacionesYNoEncontrado() {
        // Stock negativo
        Producto stockInvalido = Producto.builder().nombre("Test").stock(-1).build();
        ResponseStatusException exStock = assertThrows(ResponseStatusException.class, () -> {
            productoService.actualizarProducto(1L, stockInvalido);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exStock.getStatusCode());

        // Nombre vacío
        Producto nombreVacio = Producto.builder().nombre("").stock(5).build();
        ResponseStatusException exNombre = assertThrows(ResponseStatusException.class, () -> {
            productoService.actualizarProducto(1L, nombreVacio);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exNombre.getStatusCode());

        // ID no encontrado
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException exNoEncontrado = assertThrows(ResponseStatusException.class, () -> {
            productoService.actualizarProducto(99L, productoValido);
        });
        assertEquals(HttpStatus.NOT_FOUND, exNoEncontrado.getStatusCode());
    }

    // Consultas por ID, listado general, por categoría, stock bajo y eliminación
    @Test
    void consultarYEliminarProductos() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoValido));
        when(productoRepository.findAll()).thenReturn(List.of(productoValido));
        when(productoRepository.findByCategoria("Alimentos")).thenReturn(List.of(productoValido));
        when(productoRepository.findByStockLessThan(25)).thenReturn(List.of(productoValido));

        assertTrue(productoService.obtenerProductoPorId(1L).isPresent());
        assertEquals(1, productoService.listarProductos().size());
        assertEquals(1, productoService.listarProductosPorCategoria("Alimentos").size());
        assertEquals(1, productoService.listarProductosConStockBajo(25).size());

        productoService.eliminarProducto(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }
}