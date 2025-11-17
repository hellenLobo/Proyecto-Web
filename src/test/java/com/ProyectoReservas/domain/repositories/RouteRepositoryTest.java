package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Route;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RouteRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    private Route buildRoute(String code, String origin, String destination) {
        return Route.builder()
                .code(code)
                .name("Ruta " + code)
                .origin(origin)
                .destination(destination)
                .distanceKm(BigDecimal.valueOf(100))
                .durationMin(120)
                .build();
    }

    // ----------------------------------------------------------------
    // MÉTODO 1: findByCodeIgnoreCase
    // ----------------------------------------------------------------
    @Test
    @DisplayName("findByCodeIgnoreCase debe encontrar una ruta ignorando mayúsculas")
    void testFindByCodeIgnoreCase() {
        routeRepository.save(buildRoute("RT001", "Bogotá", "Tunja"));

        Optional<Route> result = routeRepository.findByCodeIgnoreCase("rt001");

        assertTrue(result.isPresent());
        assertEquals("RT001", result.get().getCode());
    }

    // ----------------------------------------------------------------
    // MÉTODO 2: existsByCodeIgnoreCase
    // ----------------------------------------------------------------
    @Test
    @DisplayName("existsByCodeIgnoreCase debe devolver true si la ruta existe")
    void testExistsByCodeIgnoreCase() {
        routeRepository.save(buildRoute("RT100", "Medellín", "Cali"));

        assertTrue(routeRepository.existsByCodeIgnoreCase("rt100"));
        assertFalse(routeRepository.existsByCodeIgnoreCase("rt999"));
    }

    // ----------------------------------------------------------------
    // MÉTODO 3: findByOriginIgnoreCaseAndDestinationIgnoreCase
    // (Método generado por Spring Data)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("findByOriginIgnoreCaseAndDestinationIgnoreCase debe devolver la ruta exacta")
    void testFindByOriginIgnoreCaseAndDestinationIgnoreCase() {
        routeRepository.save(buildRoute("RT200", "Bogotá", "Tunja"));

        List<Route> result =
                routeRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase("bogotá", "tunja");

        assertEquals(1, result.size());
        assertEquals("RT200", result.get(0).getCode());
    }

    // ----------------------------------------------------------------
    // MÉTODO 4: findByOriginAndDestination
    // (ESTE ES EL QUERY JPQL QUE FALTABA TESTEAR)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("findByOriginAndDestination (@Query) debe devolver la ruta exacta")
    void testFindByOriginAndDestination() {
        routeRepository.save(buildRoute("RT300", "Cali", "Palmira"));

        List<Route> result =
                routeRepository.findByOriginAndDestination("cali", "palmira");

        assertEquals(1, result.size());
        assertEquals("RT300", result.get(0).getCode());
    }
}
