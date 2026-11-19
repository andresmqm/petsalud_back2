package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    // Creación de usuario con datos completos y verificación de que email, nombre y rol no sean nulos ni vacíos
    @Test
    void crearUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("andres@petsalud.cl")
                .password("password123")
                .nombre("Andrés")
                .apellido("Quintero")
                .telefono("912345678")
                .direccion("Av. Grecia 1234, Ñuñoa")
                .rol(Rol.CLIENTE)
                .mascotas(new ArrayList<>())
                .ventas(new ArrayList<>())
                .recompensas(new ArrayList<>())
                .notificaciones(new ArrayList<>())
                .build();

        assertNotNull(usuario);
        assertNotNull(usuario.getEmail());
        assertFalse(usuario.getEmail().isBlank());
        assertNotNull(usuario.getNombre());
        assertFalse(usuario.getNombre().isBlank());
        assertNotNull(usuario.getRol());
        assertEquals(Rol.CLIENTE, usuario.getRol());
        assertEquals("Quintero", usuario.getApellido());
        assertTrue(usuario.getMascotas().isEmpty());
    }

    // Validación de que se puedan asignar todos los roles permitidos en el sistema (CLIENTE, VETERINARIO, ADMIN)
    @Test
    void validarRolesUsuario() {
        Usuario cliente = Usuario.builder().email("c@petsalud.cl").nombre("Cliente").rol(Rol.CLIENTE).build();
        Usuario veterinario = Usuario.builder().email("v@petsalud.cl").nombre("Vet").rol(Rol.VETERINARIO).build();
        Usuario admin = Usuario.builder().email("a@petsalud.cl").nombre("Admin").rol(Rol.ADMIN).build();

        assertEquals(Rol.CLIENTE, cliente.getRol());
        assertEquals(Rol.VETERINARIO, veterinario.getRol());
        assertEquals(Rol.ADMIN, admin.getRol());
    }

    // Verificación de que un usuario con rol CLIENTE pueda tener una lista de mascotas asociadas con datos válidos
    @Test
    void asociarMascotasACliente() {
        Mascota mascota1 = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Labrador")
                .build();

        Mascota mascota2 = Mascota.builder()
                .id(11L)
                .nombre("Luna")
                .especie("Gato")
                .raza("Siamés")
                .build();

        Usuario cliente = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .mascotas(List.of(mascota1, mascota2))
                .build();

        assertEquals(Rol.CLIENTE, cliente.getRol());
        assertNotNull(cliente.getMascotas());
        assertEquals(2, cliente.getMascotas().size());
        assertEquals("Max", cliente.getMascotas().get(0).getNombre());
        assertEquals("Perro", cliente.getMascotas().get(0).getEspecie());
        assertEquals("Luna", cliente.getMascotas().get(1).getNombre());
        assertEquals("Gato", cliente.getMascotas().get(1).getEspecie());
    }

    // Comparación de igualdad entre dos instancias de Usuario con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        Usuario usuario1 = Usuario.builder()
                .id(1L)
                .email("andres@petsalud.cl")
                .nombre("Andrés")
                .rol(Rol.CLIENTE)
                .build();

        Usuario usuario2 = Usuario.builder()
                .id(1L)
                .email("andres@petsalud.cl")
                .nombre("Andrés")
                .rol(Rol.CLIENTE)
                .build();

        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }
}