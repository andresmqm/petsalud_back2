package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.ExpedienteMedico;
import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.ExpedienteMedicoRepository;
import com.petsalud.backendpetsalud.repository.MascotaRepository;
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
class ExpedienteMedicoServiceImplTest {

    @Mock
    private ExpedienteMedicoRepository expedienteMedicoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ExpedienteMedicoServiceImpl expedienteMedicoService;

    private Mascota mascotaValida;
    private Usuario veterinarioValido;
    private ExpedienteMedico registroValido;

    @BeforeEach
    void setUp() {
        mascotaValida = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .build();

        veterinarioValido = Usuario.builder()
                .id(2L)
                .nombre("Dra. Camila")
                .rol(Rol.VETERINARIO)
                .build();

        registroValido = ExpedienteMedico.builder()
                .id(1L)
                .descripcion("Control preventivo")
                .diagnostico("Paciente sano")
                .tratamiento("Vacuna antirrábica")
                .build();
    }

    // Con mascota y veterinario existentes, debería asociar ambos, asignar fecha actual y guardar
    @Test
    void agregarRegistro() {
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(veterinarioValido));
        when(expedienteMedicoRepository.save(any(ExpedienteMedico.class))).thenAnswer(i -> i.getArgument(0));

        ExpedienteMedico resultado = expedienteMedicoService.agregarRegistro(10L, 2L, registroValido);

        assertNotNull(resultado);
        assertEquals(mascotaValida, resultado.getMascota());
        assertEquals(veterinarioValido, resultado.getVeterinario());
        assertNotNull(resultado.getFecha());
        verify(expedienteMedicoRepository, times(1)).save(registroValido);
    }

    // Cuando la mascota no existe en el sistema, debería lanzar IllegalArgumentException y no guardar
    @Test
    void agregarRegistroMascotaNoEncontrada() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            expedienteMedicoService.agregarRegistro(99L, 2L, registroValido);
        });

        assertEquals("Mascota no encontrada", ex.getMessage());
        verify(expedienteMedicoRepository, never()).save(any());
    }

    // Cuando el veterinario no existe en el sistema, debería lanzar IllegalArgumentException y no guardar
    @Test
    void agregarRegistroVeterinarioNoEncontrado() {
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            expedienteMedicoService.agregarRegistro(10L, 99L, registroValido);
        });

        assertEquals("Veterinario no encontrado", ex.getMessage());
        verify(expedienteMedicoRepository, never()).save(any());
    }

    // Búsqueda por ID, listado de registros médicos por mascota y eliminación de registro
    @Test
    void consultarYEliminarRegistros() {
        when(expedienteMedicoRepository.findById(1L)).thenReturn(Optional.of(registroValido));
        when(expedienteMedicoRepository.findByMascotaId(10L)).thenReturn(List.of(registroValido));

        Optional<ExpedienteMedico> encontrado = expedienteMedicoService.obtenerRegistroPorId(1L);
        List<ExpedienteMedico> historial = expedienteMedicoService.listarRegistrosPorMascota(10L);

        assertTrue(encontrado.isPresent());
        assertEquals(1, historial.size());
        assertEquals("Paciente sano", historial.get(0).getDiagnostico());

        expedienteMedicoService.eliminarRegistro(1L);
        verify(expedienteMedicoRepository, times(1)).deleteById(1L);
    }
}