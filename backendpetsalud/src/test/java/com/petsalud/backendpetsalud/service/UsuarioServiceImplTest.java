package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
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

    // Con datos completos, el cual debería encriptar la contraseña, asignar rol CLIENTE y guardar
    @Test
    void registrarUsuario() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuarioValido);

        assertNotNull(resultado);
        assertEquals(Rol.CLIENTE, resultado.getRol());
        assertEquals("hashedPassword", resultado.getPassword());
        verify(usuarioRepository, times(1)).save(usuarioValido);
    }

    // Con datos incompletos (campos obligatorios nulos), el cual debería lanzar una excepción y no guardar
    @Test
    void registrarUsuarioIncompleto() {
        Usuario usuarioIncompleto = Usuario.builder()
                .nombre("Carlos")
                .email("carlos@petsalud.cl")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioIncompleto);
        });

        verify(usuarioRepository, never()).save(any());
    }

    // Con un email ya registrado en el sistema, el cual debería lanzar una excepción y no guardar
    @Test
    void registrarUsuarioEmailDuplicado() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Optional.of(usuarioValido));

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioValido);
        });

        verify(usuarioRepository, never()).save(any());
    }

    // Búsqueda por ID y por email existente, el cual debería retornar el usuario con un rol válido
    @Test
    void obtenerUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.findByEmail("andres@petsalud.cl")).thenReturn(Optional.of(usuarioValido));

        Optional<Usuario> porId = usuarioService.obtenerUsuarioPorId(1L);
        Optional<Usuario> porEmail = usuarioService.obtenerUsuarioPorEmail("andres@petsalud.cl");

        assertTrue(porId.isPresent());
        assertEquals(Rol.CLIENTE, porId.get().getRol());
        assertTrue(porEmail.isPresent());
    }

    // Con un usuario existente, el cual debería actualizar nombre, apellido, teléfono y dirección
    @Test
    void actualizarUsuario() {
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

    // Listar todos los usuarios y eliminar un usuario por su ID
    @Test
    void listarYEliminarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioValido));

        List<Usuario> lista = usuarioService.listarUsuarios();
        assertEquals(1, lista.size());

        usuarioService.eliminarUsuario(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    // Cuando se intenta actualizar un usuario cuyo ID no existe, el cual debería lanzar una excepción
    @Test
    void actualizarUsuarioNoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarUsuario(99L, usuarioValido);
        });

        verify(usuarioRepository, never()).save(any());
    }
}