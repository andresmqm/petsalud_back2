package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Recompensa;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.RecompensaRepository;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecompensaServiceImplTest {

    @Mock
    private RecompensaRepository recompensaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RecompensaServiceImpl recompensaService;

    private Usuario usuarioValido;
    private Recompensa recompensaValida;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        recompensaValida = Recompensa.builder()
                .id(1L)
                .puntos(200)
                .descripcion("Bono por consulta preventiva")
                .build();
    }

    // Con usuario existente, debería asociar el usuario, asignar fecha actual y guardar la recompensa
    @Test
    void agregarRecompensa() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioValido));
        when(recompensaRepository.save(any(Recompensa.class))).thenAnswer(i -> i.getArgument(0));

        Recompensa resultado = recompensaService.agregarRecompensa(1L, recompensaValida);

        assertNotNull(resultado);
        assertEquals(usuarioValido, resultado.getUsuario());
        assertNotNull(resultado.getFecha());
        assertEquals(200, resultado.getPuntos());
        verify(recompensaRepository, times(1)).save(recompensaValida);
    }

    // Cuando el usuario no existe en el sistema, debería lanzar IllegalArgumentException y no guardar
    @Test
    void agregarRecompensaUsuarioNoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            recompensaService.agregarRecompensa(99L, recompensaValida);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(recompensaRepository, never()).save(any());
    }

    // Búsqueda por ID, listado de recompensas por usuario y eliminación de recompensa
    @Test
    void consultarYEliminarRecompensas() {
        when(recompensaRepository.findById(1L)).thenReturn(Optional.of(recompensaValida));
        when(recompensaRepository.findByUsuarioId(1L)).thenReturn(List.of(recompensaValida));

        Optional<Recompensa> encontrada = recompensaService.obtenerRecompensaPorId(1L);
        List<Recompensa> lista = recompensaService.listarRecompensasPorUsuario(1L);

        assertTrue(encontrada.isPresent());
        assertEquals(1, lista.size());
        assertEquals(200, lista.get(0).getPuntos());

        recompensaService.eliminarRecompensa(1L);
        verify(recompensaRepository, times(1)).deleteById(1L);
    }
}