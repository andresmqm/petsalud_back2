package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Cita;
import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.CitaRepository;
import com.petsalud.backendpetsalud.repository.MascotaRepository;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Inyecta el administrador de entidades de JPA
    @PersistenceContext 
    private EntityManager entityManager;

    @Override
    @Transactional
    public Cita agendarCita(Long mascotaId, Long veterinarioId, LocalDateTime fechaHora, String motivo) {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

        Usuario veterinario = usuarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

        if (veterinario.getRol() != Rol.VETERINARIO) {
            throw new IllegalArgumentException("El usuario seleccionado no es veterinario");
        }

        // Serializa los agendamientos de este veterinario hasta terminar la transacción.
        entityManager.lock(veterinario, LockModeType.PESSIMISTIC_WRITE);
        
        if (citaRepository.existsByVeterinarioIdAndFechaHora(veterinarioId, fechaHora)) {
            throw new IllegalArgumentException(
                    "El veterinario no está disponible en la fecha y hora seleccionadas");
        }

        Cita cita = Cita.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .fechaHora(fechaHora)
                .motivo(motivo == null ? null : Jsoup.clean(motivo, Safelist.none())) // Limpia el motivo de etiquetas HTML.
                .estado("Agendada")
                .build();

        return citaRepository.save(cita);
    }

    @Override
    public Optional<Cita> obtenerCitaPorId(Long id) {
        return citaRepository.findById(id);
    }

    @Override
    public Cita actualizarEstadoCita(Long id, String nuevoEstado) {
        return citaRepository.findById(id).map(cita -> {
            cita.setEstado(nuevoEstado);
            return citaRepository.save(cita);
        }).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
    }

    @Override
    public void cancelarCita(Long id) {
        citaRepository.deleteById(id);
    }

    @Override
    public List<Cita> listarCitasPorMascota(Long mascotaId) {
        return citaRepository.findByMascotaId(mascotaId);
    }

    @Override
    public List<Cita> listarCitasPorVeterinario(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public List<Cita> listarCitasEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByFechaHoraBetween(inicio, fin);
    }
}

