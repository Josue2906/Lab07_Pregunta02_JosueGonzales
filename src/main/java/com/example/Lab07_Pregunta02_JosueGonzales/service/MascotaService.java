package com.example.Lab07_Pregunta02_JosueGonzales.service;

import com.example.Lab07_Pregunta02_JosueGonzales.model.Mascota;
import com.example.Lab07_Pregunta02_JosueGonzales.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {
    @Autowired
    private MascotaRepository mascotaRepository;

    // Listar todos las mascotas:
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    // Guardar una mascota (crear o actualizar):
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    // Buscar una mascota por su ID:
    public Optional<Mascota> buscarPorId(Long id) {
        return mascotaRepository.findById(id);
    }

    // Eliminar una mascota por su ID:
    public void eliminar(Long id) {
        mascotaRepository.deleteById(id);
    }
}
