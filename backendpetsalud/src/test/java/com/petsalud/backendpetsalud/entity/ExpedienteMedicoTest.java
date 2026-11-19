package com.petsalud.backendpetsalud.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExpedienteMedicoTest {

    // Creación de expediente médico con datos completos y relaciones con mascota y veterinario
    @Test
    void crearExpedienteMedico() {
        Mascota mascota = Mascota.builder()
                .id(10L)
                .nombre("Max")
                .especie("Perro")
                .build();

        Usuario veterinario = Usuario.builder()
                .id(2L)
                .nombre("Dra. Camila")
                .rol(Rol.VETERINARIO)
                .build();

        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 10, 30);

        ExpedienteMedico expediente = ExpedienteMedico.builder()
                .id(1L)
                .fecha(fecha)
                .descripcion("Control preventivo anual")
                .diagnostico("Paciente sano")
                .tratamiento("Vacuna séxtuple y desparasitación")
                .mascota(mascota)
                .veterinario(veterinario)
                .build();

        assertNotNull(expediente);
        assertEquals(1L, expediente.getId());
        assertEquals(fecha, expediente.getFecha());
        assertEquals("Control preventivo anual", expediente.getDescripcion());
        assertEquals("Paciente sano", expediente.getDiagnostico());
        assertEquals("Vacuna séxtuple y desparasitación", expediente.getTratamiento());
        assertEquals(mascota, expediente.getMascota());
        assertEquals(veterinario, expediente.getVeterinario());
    }

    // Comparación de igualdad entre dos instancias de ExpedienteMedico con los mismos atributos (Lombok @Data)
    @Test
    void validarEqualsYHashCode() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 18, 10, 30);

        ExpedienteMedico exp1 = ExpedienteMedico.builder()
                .id(1L)
                .fecha(fecha)
                .descripcion("Control preventivo anual")
                .diagnostico("Paciente sano")
                .tratamiento("Vacuna séxtuple")
                .build();

        ExpedienteMedico exp2 = ExpedienteMedico.builder()
                .id(1L)
                .fecha(fecha)
                .descripcion("Control preventivo anual")
                .diagnostico("Paciente sano")
                .tratamiento("Vacuna séxtuple")
                .build();

        assertEquals(exp1, exp2);
        assertEquals(exp1.hashCode(), exp2.hashCode());
    }
}