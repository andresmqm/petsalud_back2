package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Cita;
import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.CitaRepository;
import com.petsalud.backendpetsalud.repository.MascotaRepository;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Usuario propietarioValido;
    private Usuario veterinarioValido;
    private Mascota mascotaValida;
    private LocalDateTime fechaCita;

    @BeforeEach
    void setUp() {
        propietarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .apellido("Quintero")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        veterinarioValido = Usuario.builder()
                .id(2L)
                .nombre("Dra. Camila")
                .apellido("Rojas")
                .email("camila.vet@petsalud.cl")
                .rol(Rol.VETERINARIO)
                .build();

        mascotaValida = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Golden Retriever")
                .propietario(propietarioValido)
                .build();

        fechaCita = LocalDateTime.of(2026, 11, 25, 10, 30);
    }

    // Con datos válidos, el cual debería guardar la cita con estado "Agendada"
    @Test
    void agendarCita() {

        // 1. Simulamos que existen la mascota, el veterinario y que el horario está libre

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(veterinarioValido));
        when(citaRepository.existsByVeterinarioIdAndFechaHora(2L, fechaCita)).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        // 2. Ejecutamos el método

        Cita resultado = citaService.agendarCita(10L, 2L, fechaCita, "Control anual <b>urgente</b>");

        // 3. Verificamos resultados (Cumple Paso 2 de la guía)

        assertNotNull(resultado);
        assertEquals("Agendada", resultado.getEstado());
        assertEquals("Control anual urgente", resultado.getMotivo()); // Verifica limpieza Jsoup
        assertEquals(veterinarioValido, resultado.getVeterinario());
        assertEquals(mascotaValida, resultado.getMascota());
        assertNotNull(resultado.getMascota().getPropietario()); // Verifica dueño registrado

        verify(entityManager, times(1)).lock(veterinarioValido, LockModeType.PESSIMISTIC_WRITE);
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    // Con horario ocupado, el cual debería lanzar una excepción y no guardar la cita
    @Test
    void agendarCitaHorarioOcupado() {
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(veterinarioValido));

        // Simulamos que el veterinario YA tiene una cita a esa hora

        when(citaRepository.existsByVeterinarioIdAndFechaHora(2L, fechaCita)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            citaService.agendarCita(10L, 2L, fechaCita, "Vacunación");
        });

        assertEquals("El veterinario no está disponible en la fecha y hora seleccionadas", ex.getMessage());
        verify(citaRepository, never()).save(any());
    }

    // Cuando el usuario asignado no tiene rol VETERINARIO, el cual debería lanzar una excepción
    @Test
    void agendarCitaRolInvalido() {

        // Pasamos el ID del propietario (Rol.CLIENTE) en lugar de un veterinario
        
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietarioValido));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            citaService.agendarCita(10L, 1L, fechaCita, "Consulta");
        });

        assertEquals("El usuario seleccionado no es veterinario", ex.getMessage());
        verify(citaRepository, never()).save(any());
    }

    // Cuando la mascota o el veterinario no existen en la base de datos, el cual debería lanzar una excepción
    @Test
    void agendarCitaNoEncontrado() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            citaService.agendarCita(99L, 2L, fechaCita, "Consulta");
        });

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaValida));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            citaService.agendarCita(10L, 99L, fechaCita, "Consulta");
        });
    }

    // Con una cita existente, el cual debería actualizar su estado correctamente (ej. a "Completada")
    @Test
    void actualizarEstadoCita() {
        Cita citaExistente = Cita.builder().id(100L).estado("Agendada").build();
        when(citaRepository.findById(100L)).thenReturn(Optional.of(citaExistente));
        when(citaRepository.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        Cita actualizada = citaService.actualizarEstadoCita(100L, "Completada");

        assertEquals("Completada", actualizada.getEstado());
        verify(citaRepository, times(1)).save(citaExistente);
    }

    // Consultas por ID, mascota, veterinario, rango de fechas y eliminación (cancelación) de cita
    @Test
    void consultarYCancelarCitas() {
        Cita c = Cita.builder().id(100L).mascota(mascotaValida).veterinario(veterinarioValido).build();

        when(citaRepository.findById(100L)).thenReturn(Optional.of(c));
        when(citaRepository.findByMascotaId(10L)).thenReturn(List.of(c));
        when(citaRepository.findByVeterinarioId(2L)).thenReturn(List.of(c));
        when(citaRepository.findByFechaHoraBetween(any(), any())).thenReturn(List.of(c));

        assertTrue(citaService.obtenerCitaPorId(100L).isPresent());
        assertEquals(1, citaService.listarCitasPorMascota(10L).size());
        assertEquals(1, citaService.listarCitasPorVeterinario(2L).size());
        assertEquals(1, citaService.listarCitasEntreFechas(fechaCita.minusDays(1), fechaCita.plusDays(1)).size());

        citaService.cancelarCita(100L);
        verify(citaRepository, times(1)).deleteById(100L);
    }
}