package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Bus;
import com.ProyectoReservas.domain.entities.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByPlate(String plate);
    boolean existsByPlateIgnoreCase(String plate);
    List<Bus> findByStatus(BusStatus status);

}

