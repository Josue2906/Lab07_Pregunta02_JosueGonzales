package com.example.Lab07_Pregunta02_JosueGonzales.repository;

import com.example.Lab07_Pregunta02_JosueGonzales.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}
