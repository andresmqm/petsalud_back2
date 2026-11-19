package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.MascotaRepository;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Usuario propietarioValido;
    private Mascota mascotaValida;

    @BeforeEach
    void setUp() {
        propietarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .apellido("Quintero")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        mascotaValida = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Golden Retriever")
                .fechaNacimiento(LocalDate.of(2022, 5, 10))
                .sexo("Macho")
                .build();
    }

    // Con propietario CLIENTE existente y datos válidos (nombre y especie), debería asociar el dueño y guardar
    @Test
    void registrarMascota() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietarioValido));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(i -> i.getArgument(0));

        Mascota resultado = mascotaService.registrarMascota(mascotaValida, 1L);

        assertNotNull(resultado);
        assertEquals("Max", resultado.getNombre());
        assertFalse(resultado.getNombre().isBlank());
        assertEquals("Perro", resultado.getEspecie());
        assertFalse(resultado.getEspecie().isBlank());
        assertEquals(propietarioValido, resultado.getPropietario());
        assertEquals(Rol.CLIENTE, resultado.getPropietario().getRol());
        verify(mascotaRepository, times(1)).save(mascotaValida);
    }

    // Cuando el propietario no existe en la base de datos, debería lanzar una excepción y no guardar
    @Test
    void registrarMascotaPropietarioNoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascotaValida, 99L);
        });

        assertEquals("Propietario no encontrado", ex.getMessage());
        verify(mascotaRepository, never()).save(any());
    }

    // Con una mascota existente, debería actualizar nombre, especie, raza, fecha de nacimiento y sexo
    @Test
    void actualizarMascota() {
        Mascota datosNuevos = Mascota.builder()
                .nombre("Maximus")
                .especie("Perro")
                .raza("Labrador")
                .fechaNacimiento(LocalDate.of(2022, 5, 10))
                .sexo("Macho")
                .build();

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(i -> i.getArgument(0));

        Mascota actualizada = mascotaService.actualizarMascota(10L, datosNuevos);

        assertEquals("Maximus", actualizada.getNombre());
        assertEquals("Labrador", actualizada.getRaza());
        verify(mascotaRepository, times(1)).save(mascotaValida);
    }

    // Cuando se intenta actualizar una mascota cuyo ID no existe, debería lanzar una excepción
    @Test
    void actualizarMascotaNoEncontrada() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.actualizarMascota(99L, mascotaValida);
        });

        assertEquals("Mascota no encontrada", ex.getMessage());
        verify(mascotaRepository, never()).save(any());
    }

    // Búsqueda por ID, listado de mascotas por propietario y eliminación de mascota
    @Test
    void consultarYEliminarMascotas() {
        mascotaValida.setPropietario(propietarioValido);

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(mascotaRepository.findByPropietarioId(1L)).thenReturn(List.of(mascotaValida));

        Optional<Mascota> encontrada = mascotaService.obtenerMascotaPorId(10L);
        List<Mascota> lista = mascotaService.listarMascotasPorPropietario(1L);

        assertTrue(encontrada.isPresent());
        assertEquals(1, lista.size());
        assertEquals("Max", lista.get(0).getNombre());

        mascotaService.eliminarMascota(10L);
        verify(mascotaRepository, times(1)).deleteById(10L);
    }
}