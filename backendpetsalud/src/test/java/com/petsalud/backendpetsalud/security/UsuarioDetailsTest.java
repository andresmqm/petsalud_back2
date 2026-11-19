package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDetailsTest {

    // Verificación de credenciales, autoridades con prefijo ROLE_ y estados activos de cuenta
    @Test
    void validarPropiedadesUsuarioDetails() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("andres@petsalud.cl")
                .password("$2a$10$hashedPassword")
                .rol(Rol.GERENTE)
                .build();

        UsuarioDetails details = new UsuarioDetails(usuario);

        assertEquals("andres@petsalud.cl", details.getUsername());
        assertEquals("$2a$10$hashedPassword", details.getPassword());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());

        GrantedAuthority authority = details.getAuthorities().iterator().next();
        assertEquals("ROLE_GERENTE", authority.getAuthority());
    }
}