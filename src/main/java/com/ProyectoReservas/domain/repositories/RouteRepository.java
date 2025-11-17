package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<Route> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
    

    //Buscar rutas que conecten dos ciudades (origen y destino).
    @Query("""
       SELECT r FROM Route r
       WHERE LOWER(r.origin) = LOWER(:origin)
         AND LOWER(r.destination) = LOWER(:destination)
       """)
    List<Route> findByOriginAndDestination(@Param("origin") String origin,
                                           @Param("destination") String destination);

}

