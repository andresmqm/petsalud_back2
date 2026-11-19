package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MascotaTest {

    // Creación de mascota con datos completos y verificación de que nombre, especie y propietario sean válidos
    @Test
    void crearMascota() {
        Usuario propietario = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        LocalDate fechaNac = LocalDate.of(2022, 5, 10);

        Mascota mascota = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Golden Retriever")
                .fechaNacimiento(fechaNac)
                .sexo("Macho")
                .propietario(propietario)
                .citas(new ArrayList<>())
                .expedientesMedicos(new ArrayList<>())
                .build();

        assertNotNull(mascota);
        assertEquals(10L, mascota.getId());
        assertNotNull(mascota.getNombre());
        assertFalse(mascota.getNombre().isBlank());
        assertEquals("Max", mascota.getNombre());
        assertNotNull(mascota.getEspecie());
        assertFalse(mascota.getEspecie().isBlank());
        assertEquals("Perro", mascota.getEspecie());
        assertEquals("Golden Retriever", mascota.getRaza());
        assertEquals(fechaNac, mascota.getFechaNacimiento());
        assertEquals("Macho", mascota.getSexo());
        assertEquals(propietario, mascota.getPropietario());
        assertTrue(mascota.getCitas().isEmpty());
        assertTrue(mascota.getExpedientesMedicos().isEmpty());
    }

    // Comparación de igualdad entre dos instancias de Mascota con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        Mascota mascota1 = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Golden Retriever")
                .build();

        Mascota mascota2 = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .raza("Golden Retriever")
                .build();

        assertEquals(mascota1, mascota2);
        assertEquals(mascota1.hashCode(), mascota2.hashCode());
    }
}