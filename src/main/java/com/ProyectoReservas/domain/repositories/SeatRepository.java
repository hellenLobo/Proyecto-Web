package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    boolean existsByBusIdAndNumber(Long busId, Integer number);

    Optional<Seat> findByBusIdAndNumber(Long busId, Integer number);
}


