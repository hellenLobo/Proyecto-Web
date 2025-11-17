package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Trip;
import com.ProyectoReservas.domain.entities.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByStatus(TripStatus status);

    //Buscar viajes disponibles entre fechas, ruta y estado SCHEDULED.
    @Query("""
       SELECT t FROM Trip t
       WHERE t.route.id = :routeId
         AND DATE(t.departureAt) = DATE(:date)
         AND t.status = 'SCHEDULED'
       ORDER BY t.departureAt
       """)
    List<Trip> findAvailableTrips(@Param("routeId") Long routeId,
                                  @Param("date") OffsetDateTime date);

    //Contar cuántos asientos están ocupados o disponibles.
    @Query("""
       SELECT COUNT(DISTINCT tk.seatNumber)
       FROM Ticket tk
       WHERE tk.trip.id = :tripId
         AND tk.status = 'SOLD'
       """)
    long countOccupiedSeats(@Param("tripId") Long tripId);

}

