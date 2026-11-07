package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.repository.MascotaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component("mascotaSecurity")
public class MascotaSecurity {

    private final MascotaRepository mascotaRepository;

    public MascotaSecurity(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    // Comprueba si la mascota pertenece al usuario de la solicitud.
    public boolean esPropietario(Long mascotaId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || mascotaId == null) {
            return false;
        }

        // Busca la mascota usando su ID y el correo del usuario autenticado.
        return mascotaRepository.existsByIdAndPropietarioEmail(
                mascotaId, authentication.getName());
    }
}