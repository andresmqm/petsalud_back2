package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Producto;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.entity.Venta;
import com.petsalud.backendpetsalud.repository.ProductoRepository;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import com.petsalud.backendpetsalud.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Usuario clienteValido;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        clienteValido = Usuario.builder()
                .id(1L)
                .nombre("Laura")
                .apellido("Gómez")
                .email("laura@petsalud.cl")
                .direccion("Av. Irarrázaval 2000")
                .rol(Rol.CLIENTE)
                .build();

        producto1 = Producto.builder()
                .id(10L)
                .nombre("Antiparasitario")
                .precio(new BigDecimal("12500"))
                .stock(5)
                .build();

        producto2 = Producto.builder()
                .id(20L)
                .nombre("Alimento Premium 3kg")
                .precio(new BigDecimal("24500"))
                .stock(3)
                .build();
    }

    // Con stock y usuario CLIENTE válido, el cual debería calcular el total, descontar stock y guardar la venta
    @Test
    void realizarVenta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(productoRepository.findAllById(List.of(10L, 20L))).thenReturn(List.of(producto1, producto2));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        Venta venta = ventaService.realizarVenta(1L, List.of(10L, 20L));

        assertNotNull(venta);
        assertEquals(new BigDecimal("37000"), venta.getTotal());
        assertEquals(4, producto1.getStock());
        assertEquals(2, producto2.getStock());
        assertEquals(2, venta.getProductos().size());
        assertEquals("laura@petsalud.cl", venta.getUsuario().getEmail());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    // Cuando un producto tiene stock en 0, el cual debería lanzar una excepción y no guardar la venta
    @Test
    void realizarVentaSinStock() {
        producto1.setStock(0);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(productoRepository.findAllById(List.of(10L))).thenReturn(List.of(producto1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ventaService.realizarVenta(1L, List.of(10L));
        });

        assertTrue(ex.getMessage().contains("no tiene stock disponible"));
        verify(ventaRepository, never()).save(any());
    }

    // Cuando el usuario tiene datos incompletos (ej. dirección nula), el cual debería lanzar una excepción
    @Test
    void realizarVentaUsuarioIncompleto() {
        Usuario usuarioSinDireccion = Usuario.builder()
                .id(2L)
                .nombre("Pedro")
                .apellido("Soto")
                .email("pedro@petsalud.cl")
                .direccion(null)
                .rol(Rol.CLIENTE)
                .build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioSinDireccion));

        assertThrows(IllegalArgumentException.class, () -> {
            ventaService.realizarVenta(2L, List.of(10L));
        });

        verify(ventaRepository, never()).save(any());
    }

    // Cuando el usuario no tiene rol CLIENTE (ej. VETERINARIO), el cual debería lanzar una excepción
    @Test
    void realizarVentaRolNoAutorizado() {
        clienteValido.setRol(Rol.VETERINARIO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ventaService.realizarVenta(1L, List.of(10L));
        });

        assertTrue(ex.getMessage().contains("no está autorizado"));
        verify(ventaRepository, never()).save(any());
    }

    // Cuando uno de los IDs de producto no existe en la base de datos, el cual debería lanzar una excepción
    @Test
    void realizarVentaProductoInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(productoRepository.findAllById(List.of(10L, 99L))).thenReturn(List.of(producto1));

        assertThrows(IllegalArgumentException.class, () -> {
            ventaService.realizarVenta(1L, List.of(10L, 99L));
        });

        verify(ventaRepository, never()).save(any());
    }

    // Consultas de venta por ID, listado por usuario y listado general
    @Test
    void consultarVentas() {
        Venta v = Venta.builder().id(100L).usuario(clienteValido).total(new BigDecimal("12500")).build();

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(v));
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(v));
        when(ventaRepository.findAll()).thenReturn(List.of(v));

        assertTrue(ventaService.obtenerVentaPorId(100L).isPresent());
        assertEquals(1, ventaService.listarVentasPorUsuario(1L).size());
        assertEquals(1, ventaService.listarTodasLasVentas().size());
    }
}