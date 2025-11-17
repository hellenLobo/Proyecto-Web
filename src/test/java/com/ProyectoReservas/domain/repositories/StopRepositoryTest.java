package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Route;
import com.ProyectoReservas.domain.entities.Stop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StopRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT1")
                        .name("Ruta ejemplo")
                        .origin("Ciudad A")
                        .destination("Ciudad B")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(120)
                        .build()
        );
    }

    private Stop buildStop(Route route, String name, int pos) {
        return Stop.builder()
                .route(route)
                .name(name)
                .position(pos)
                .lat(BigDecimal.valueOf(10.0 + pos))
                .lng(BigDecimal.valueOf(20.0 + pos))
                .build();
    }

    // -----------------------------------------------------------------------
    // MÉTODO 1: findByRouteIdOrderByPositionAsc
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("findByRouteIdOrderByPositionAsc debe devolver las paradas ordenadas por posición")
    void testFindByRouteIdOrderByPositionAsc() {

        Route route = buildRoute();

        Stop s1 = stopRepository.save(buildStop(route, "A", 1));
        Stop s2 = stopRepository.save(buildStop(route, "B", 2));
        Stop s3 = stopRepository.save(buildStop(route, "C", 3));

        List<Stop> result = stopRepository.findByRouteIdOrderByPositionAsc(route.getId());

        assertEquals(3, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
        assertEquals("C", result.get(2).getName());
    }

    // -----------------------------------------------------------------------
    // MÉTODO 2: findStopsByRoute (JPQL @Query)
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("findStopsByRoute (@Query) debe devolver las paradas ordenadas por posición")
    void testFindStopsByRoute() {

        Route route = buildRoute();

        stopRepository.save(buildStop(route, "A", 1));
        stopRepository.save(buildStop(route, "B", 2));

        List<Stop> result = stopRepository.findStopsByRoute(route.getId());

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getPosition());
        assertEquals(2, result.get(1).getPosition());
    }

    // -----------------------------------------------------------------------
    // MÉTODO 3: findByRouteIdAndPosition
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("findByRouteIdAndPosition debe encontrar una parada específica")
    void testFindByRouteIdAndPosition() {

        Route route = buildRoute();

        stopRepository.save(buildStop(route, "Start", 1));
        stopRepository.save(buildStop(route, "Middle", 2));

        Optional<Stop> middle = stopRepository.findByRouteIdAndPosition(route.getId(), 2);

        assertTrue(middle.isPresent());
        assertEquals("Middle", middle.get().getName());
    }

    // -----------------------------------------------------------------------
    // OPCIONAL: Validar la unicidad (route_id, position)
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("No debe permitir dos paradas con la misma posición en la misma ruta")
    void testUniqueConstraint() {

        Route route = buildRoute();

        stopRepository.save(buildStop(route, "A", 1));

        assertThrows(Exception.class, () -> {
            stopRepository.save(buildStop(route, "Duplicado", 1));
            stopRepository.flush();
        });
    }
}
