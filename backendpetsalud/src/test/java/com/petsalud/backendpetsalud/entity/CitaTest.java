package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CitaTest {

    @Test
    void testCrearYValidarCita() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 25, 15, 0);
        Usuario propietario = Usuario.builder().id(1L).nombre("Andrés").rol(Rol.CLIENTE).build();
        Usuario vet = Usuario.builder().id(2L).nombre("Dra. Ana").rol(Rol.VETERINARIO).build();
        Mascota mascota = Mascota.builder().id(5L).nombre("Luna").especie("Gato").propietario(propietario).build();

        Cita cita = Cita.builder()
                .id(1L)
                .fechaHora(fecha)
                .estado("Agendada")
                .motivo("Control general")
                .mascota(mascota)
                .veterinario(vet)
                .build();

        assertNotNull(cita);
        assertEquals(1L, cita.getId());
        assertEquals(fecha, cita.getFechaHora());
        assertEquals("Agendada", cita.getEstado());
        assertEquals("Control general", cita.getMotivo());
        assertEquals("Luna", cita.getMascota().getNombre());
        assertEquals("Dra. Ana", cita.getVeterinario().getNombre());
    }

    @Test
    void testRelacionUsuarioMascotasYRoles() {
        Mascota m1 = Mascota.builder().id(1L).nombre("Rocky").especie("Perro").build();
        Mascota m2 = Mascota.builder().id(2L).nombre("Mishi").especie("Gato").build();

        Usuario cliente = Usuario.builder()
                .id(10L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .mascotas(List.of(m1, m2))
                .build();

        Usuario admin = Usuario.builder().id(11L).rol(Rol.ADMIN).build();
        Usuario vet = Usuario.builder().id(12L).rol(Rol.VETERINARIO).build();

        // Verifica rol válido y lista de mascotas asociada (Paso 3 de la guía)
        assertEquals(Rol.CLIENTE, cliente.getRol());
        assertEquals(Rol.ADMIN, admin.getRol());
        assertEquals(Rol.VETERINARIO, vet.getRol());
        assertEquals(2, cliente.getMascotas().size());
        assertEquals("Perro", cliente.getMascotas().get(0).getEspecie());
    }
}