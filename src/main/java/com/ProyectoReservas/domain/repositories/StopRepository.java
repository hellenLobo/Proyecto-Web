package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop, Long> {

    List<Stop> findByRouteIdOrderByPositionAsc(Long routeId);

    @Query("""
           SELECT s FROM Stop s
           WHERE s.route.id = :routeId
           ORDER BY s.position ASC
           """)
    List<Stop> findStopsByRoute(@Param("routeId") Long routeId);

    Optional<Stop> findByRouteIdAndPosition(Long routeId, Integer position);
}


