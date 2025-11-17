package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Ticket;
import com.ProyectoReservas.domain.entities.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByTripId(Long tripId, Pageable pageable);
    List<Ticket> findByPassengerId(Long userId);
    List<Ticket> findByStatus(TicketStatus status);

    //Consultar todos los tickets vendidos por viaje (para despachadores o admin).
    @Query("""
       SELECT tk FROM Ticket tk
       JOIN FETCH tk.trip t
       WHERE t.id = :tripId
         AND tk.status = 'SOLD'
       ORDER BY tk.seatNumber
       """)
    List<Ticket> findSoldTicketsByTrip(@Param("tripId") Long tripId);

    //Buscar tickets de un pasajero por fecha o ruta.
    @Query("""
       SELECT tk FROM Ticket tk
       JOIN tk.trip t
       JOIN t.route r
       WHERE tk.passenger.id = :userId
         AND t.departureAt BETWEEN :startDate AND :endDate
       ORDER BY t.departureAt DESC
       """)
    List<Ticket> findTicketsByPassengerAndDateRange(@Param("userId") Long userId,
                                                    @Param("startDate") OffsetDateTime start,
                                                    @Param("endDate") OffsetDateTime end);

}

