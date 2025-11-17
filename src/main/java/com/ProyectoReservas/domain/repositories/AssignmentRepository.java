package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTripId(Long tripId);

    //Ver viajes asignados a un conductor en una fecha específica.
    @Query("""
       SELECT a FROM Assignment a
       JOIN FETCH a.trip t
       WHERE a.driver.id = :driverId
         AND DATE(t.departureAt) = DATE(:date)
       """)
    List<Assignment> findByDriverAndDate(@Param("driverId") Long driverId,
                                         @Param("date") OffsetDateTime date);


}

