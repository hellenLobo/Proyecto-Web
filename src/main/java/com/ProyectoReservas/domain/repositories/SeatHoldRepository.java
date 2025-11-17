package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.SeatHold;
import com.ProyectoReservas.domain.entities.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
    List<SeatHold> findByTripIdAndStatus(Long tripId, HoldStatus status);

    //Verificar si un asiento está temporalmente bloqueado (en hold) y aún no expiró.
    @Query("""
       SELECT COUNT(sh) > 0 FROM SeatHold sh
       WHERE sh.trip.id = :tripId
         AND sh.seatNumber = :seatNumber
         AND sh.status = 'HOLD'
         AND sh.expiresAt > CURRENT_TIMESTAMP
       """)
    boolean isSeatHeld(@Param("tripId") Long tripId,
                       @Param("seatNumber") Integer seatNumber);


}

