package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Notificacion;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.NotificacionRepository;
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
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Usuario usuarioValido;
    private Notificacion notificacionValida;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        notificacionValida = Notificacion.builder()
                .id(1L)
                .mensaje("Tu cita ha sido confirmada")
                .tipo("Recordatorio")
                .leida(false)
                .build();
    }

    // Con usuario existente, debería asociar el usuario, asignar fecha actual, marcar como no leída y guardar
    @Test
    void enviarNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioValido));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(i -> i.getArgument(0));

        Notificacion resultado = notificacionService.enviarNotificacion(1L, notificacionValida);

        assertNotNull(resultado);
        assertEquals(usuarioValido, resultado.getUsuario());
        assertNotNull(resultado.getFecha());
        assertFalse(resultado.getLeida());
        verify(notificacionRepository, times(1)).save(notificacionValida);
    }

    // Cuando el usuario destinatario no existe, debería lanzar IllegalArgumentException y no guardar
    @Test
    void enviarNotificacionUsuarioNoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.enviarNotificacion(99L, notificacionValida);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(notificacionRepository, never()).save(any());
    }

    // Marcar como leída cuando la notificación existe y cuando no existe (para cubrir ambas ramas del ifPresent)
    @Test
    void marcarComoLeida() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionValida));
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        notificacionService.marcarComoLeida(1L);
        assertTrue(notificacionValida.getLeida());
        verify(notificacionRepository, times(1)).save(notificacionValida);

        notificacionService.marcarComoLeida(99L);
        verify(notificacionRepository, times(1)).save(any());
    }

    // Búsqueda por ID, listado general por usuario, listado de no leídas y eliminación
    @Test
    void consultarYEliminarNotificaciones() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionValida));
        when(notificacionRepository.findByUsuarioId(1L)).thenReturn(List.of(notificacionValida));
        when(notificacionRepository.findByLeidaFalseAndUsuarioId(1L)).thenReturn(List.of(notificacionValida));

        assertTrue(notificacionService.obtenerNotificacionPorId(1L).isPresent());
        assertEquals(1, notificacionService.listarNotificacionesPorUsuario(1L).size());
        assertEquals(1, notificacionService.listarNotificacionesNoLeidas(1L).size());

        notificacionService.eliminarNotificacion(1L);
        verify(notificacionRepository, times(1)).deleteById(1L);
    }
}