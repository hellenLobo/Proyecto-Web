package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FareRuleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private FareRuleRepository fareRuleRepository;

    // Helpers
    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-FARE")
                        .name("Ruta de prueba")
                        .origin("Ciudad A")
                        .destination("Ciudad B")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(120)
                        .build()
        );
    }

    private Stop buildStop(Route route, String name, int pos) {
        return stopRepository.save(
                Stop.builder()
                        .route(route)
                        .name(name)
                        .position(pos)
                        .build()
        );
    }

    private FareRule buildFareRule(Route route, Stop from, Stop to, BigDecimal price) {
        return fareRuleRepository.save(
                FareRule.builder()
                        .route(route)
                        .fromStop(from)
                        .toStop(to)
                        .basePrice(price)
                        .dynamicPricing(false)
                        .build()
        );
    }

    // Tests

    @Test
    @DisplayName("Debe encontrar reglas por RouteId")
    void testFindByRouteId() {
        Route route = buildRoute();
        Stop s1 = buildStop(route, "A", 1);
        Stop s2 = buildStop(route, "B", 2);

        buildFareRule(route, s1, s2, BigDecimal.valueOf(5000));

        List<FareRule> list = fareRuleRepository.findByRouteId(route.getId());
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("Debe encontrar una regla específica de un tramo")
    void testFindByRouteIdAndStops() {
        Route route = buildRoute();
        Stop s1 = buildStop(route, "A", 1);
        Stop s2 = buildStop(route, "B", 2);

        FareRule rule = buildFareRule(route, s1, s2, BigDecimal.valueOf(5000));

        Optional<FareRule> result =
                fareRuleRepository.findByRouteIdAndFromStopIdAndToStopId(
                        route.getId(), s1.getId(), s2.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(rule.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Debe encontrar tarifa usando Query personalizada")
    void testFindFareBetweenStops() {
        Route route = buildRoute();
        Stop s1 = buildStop(route, "A", 1);
        Stop s2 = buildStop(route, "B", 2);

        FareRule rule = buildFareRule(route, s1, s2, BigDecimal.valueOf(5000));

        FareRule result =
                fareRuleRepository.findFareBetweenStops(route.getId(), s1.getId(), s2.getId());

        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    @DisplayName("No debe permitir duplicar el mismo tramo")
    void testUniqueConstraint() {
        Route route = buildRoute();
        Stop s1 = buildStop(route, "A", 1);
        Stop s2 = buildStop(route, "B", 2);

        buildFareRule(route, s1, s2, BigDecimal.valueOf(5000));

        assertThrows(Exception.class, () ->
                buildFareRule(route, s1, s2, BigDecimal.valueOf(6000))
        );
    }
}
