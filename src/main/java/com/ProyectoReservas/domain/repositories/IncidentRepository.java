package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Incident;
import com.ProyectoReservas.domain.entities.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByType(IncidentType type);

    //Buscar incidentes por tipo y rango de fechas.
    @Query("""
       SELECT i FROM Incident i
       WHERE i.type = :type
         AND i.createdAt BETWEEN :startDate AND :endDate
       ORDER BY i.createdAt DESC
       """)
    List<Incident> findByTypeAndDateRange(
            @Param("type") IncidentType type,
            @Param("startDate") OffsetDateTime start,
            @Param("endDate") OffsetDateTime end);

}

