package com.petsalud.backendpetsalud.serviceTest;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import com.petsalud.backendpetsalud.service.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .apellido("Quintero")
                .email("andres@petsalud.cl")
                .password("123456")
                .direccion("Av. Grecia 1234, Ñuñoa")
                .telefono("123456789")
                .rol(Rol.CLIENTE)
                .build();
    }

    @Test
    void registrarUsuario_ConDatosCompletos_DeberiaGuardarYAsignarRolCliente() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuarioValido);

        assertNotNull(resultado);
        assertEquals(Rol.CLIENTE, resultado.getRol());
        assertEquals("hashedPassword", resultado.getPassword());
        verify(usuarioRepository, times(1)).save(usuarioValido);
    }

    @Test
    void registrarUsuario_ConDatosIncompletos_DeberiaLanzarExcepcion() {
        Usuario usuarioIncompleto = Usuario.builder()
                .nombre("Carlos")
                .email("carlos@petsalud.cl")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioIncompleto);
        });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarUsuario_ConEmailDuplicado_DeberiaLanzarExcepcion() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Optional.of(usuarioValido));

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioValido);
        });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerUsuarioPorIdYEmail_Existente_DeberiaRetornarUsuarioConRolValido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.findByEmail("andres@petsalud.cl")).thenReturn(Optional.of(usuarioValido));

        Optional<Usuario> porId = usuarioService.obtenerUsuarioPorId(1L);
        Optional<Usuario> porEmail = usuarioService.obtenerUsuarioPorEmail("andres@petsalud.cl");

        assertTrue(porId.isPresent());
        assertEquals(Rol.CLIENTE, porId.get().getRol());
        assertTrue(porEmail.isPresent());
    }

    @Test
    void actualizarUsuario_Existente_DeberiaActualizarDatos() {
        Usuario datosNuevos = Usuario.builder()
                .nombre("Andrés Manuel")
                .apellido("Quintero Mejía")
                .telefono("987654321")
                .direccion("Nueva Dirección 456")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario actualizado = usuarioService.actualizarUsuario(1L, datosNuevos);

        assertEquals("Andrés Manuel", actualizado.getNombre());
        assertEquals("Nueva Dirección 456", actualizado.getDireccion());
    }

    @Test
    void listarYEliminarUsuarios_DeberiaEjecutarCorrectamente() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioValido));

        List<Usuario> lista = usuarioService.listarUsuarios();
        assertEquals(1, lista.size());

        usuarioService.eliminarUsuario(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}