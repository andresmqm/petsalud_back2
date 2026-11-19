package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecompensaTest {

    // Creación de recompensa con datos completos y verificación de relación con usuario
    @Test
    void crearRecompensa() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Andrés")
                .email("andres@petsalud.cl")
                .rol(Rol.CLIENTE)
                .build();

        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 18, 0);

        Recompensa recompensa = Recompensa.builder()
                .id(1L)
                .puntos(150)
                .descripcion("Puntos acumulados por compra de alimento")
                .fecha(fecha)
                .usuario(usuario)
                .build();

        assertNotNull(recompensa);
        assertEquals(1L, recompensa.getId());
        assertEquals(150, recompensa.getPuntos());
        assertEquals("Puntos acumulados por compra de alimento", recompensa.getDescripcion());
        assertEquals(fecha, recompensa.getFecha());
        assertEquals(usuario, recompensa.getUsuario());
    }

    // Comparación de igualdad entre dos instancias de Recompensa con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 18, 0);

        Recompensa recompensa1 = Recompensa.builder()
                .id(1L)
                .puntos(150)
                .descripcion("Puntos por compra")
                .fecha(fecha)
                .build();

        Recompensa recompensa2 = Recompensa.builder()
                .id(1L)
                .puntos(150)
                .descripcion("Puntos por compra")
                .fecha(fecha)
                .build();

        assertEquals(recompensa1, recompensa2);
        assertEquals(recompensa1.hashCode(), recompensa2.hashCode());
    }
}