package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    List<FareRule> findByRouteId(Long routeId);
    Optional<FareRule> findByRouteIdAndFromStopIdAndToStopId(Long routeId, Long fromStopId, Long toStopId);

//Obtener la tarifa para un tramo específico de una ruta.
    @Query("""
       SELECT fr FROM FareRule fr
       WHERE fr.route.id = :routeId
         AND fr.fromStop.id = :fromStopId
         AND fr.toStop.id = :toStopId
       """)
    FareRule findFareBetweenStops(@Param("routeId") Long routeId,
                                  @Param("fromStopId") Long fromStopId,
                                  @Param("toStopId") Long toStopId);

}

