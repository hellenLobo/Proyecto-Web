package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.*;
import com.ProyectoReservas.domain.entities.Role;
import com.ProyectoReservas.domain.entities.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeatHoldRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    // ------------------------------------------------------------------------

    private User buildUser() {
        return userRepository.save(
                User.builder()
                        .name("Test User")
                        .email("user@example.com")
                        .phone("3000000000")
                        .role(Role.PASSENGER)
                        .status(UserStatus.ACTIVE)
                        .passwordHash("hash")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-HOLD")
                        .name("Ruta Temporal")
                        .origin("Origen")
                        .destination("Destino")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(120)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("HLD123")
                        .capacity(40)
                        .amenities(null)
                        .status(BusStatus.ACTIVE)
                        .build()
        );
    }

    private Trip buildTrip(Route route, Bus bus) {
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(OffsetDateTime.now().plusHours(2))
                        .arrivalAt(OffsetDateTime.now().plusHours(5))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );
    }

    private SeatHold buildHold(Trip trip, User user, int seat, OffsetDateTime expiresAt) {
        return SeatHold.builder()
                .trip(trip)
                .user(user)
                .seatNumber(seat)
                .expiresAt(expiresAt)
                .status(HoldStatus.HOLD)
                .build();
    }

    // ------------------------------------------------------------------------
    // TEST 1: findByTripIdAndStatus
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("findByTripIdAndStatus debe devolver los holds activos del trip")
    void testFindByTripIdAndStatus() {

        User user = buildUser();
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus);

        seatHoldRepository.save(buildHold(trip, user, 1, OffsetDateTime.now().plusMinutes(5)));
        seatHoldRepository.save(buildHold(trip, user, 2, OffsetDateTime.now().plusMinutes(5)));

        List<SeatHold> result = seatHoldRepository.findByTripIdAndStatus(
                trip.getId(), HoldStatus.HOLD
        );

        assertEquals(2, result.size());
    }

    // ------------------------------------------------------------------------
    // TEST 2: isSeatHeld (JPQL)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("isSeatHeld debe detectar si un asiento está en HOLD y no expirado")
    void testIsSeatHeld() {

        User user = buildUser();
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus);

        // hold válido (no expirado)
        seatHoldRepository.save(buildHold(trip, user, 5, OffsetDateTime.now().plusMinutes(10)));

        boolean held = seatHoldRepository.isSeatHeld(trip.getId(), 5);

        assertTrue(held, "Debe estar en HOLD y no expirado");

        // hold expirado
        seatHoldRepository.save(buildHold(trip, user, 6, OffsetDateTime.now().minusMinutes(1)));

        boolean expired = seatHoldRepository.isSeatHeld(trip.getId(), 6);

        assertFalse(expired, "Si expiró, no debe considerarse HOLD");
    }

    // ------------------------------------------------------------------------
    // TEST 3: UniqueConstraint (trip_id, seat_number)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Debe fallar al intentar duplicar seatNumber para el mismo trip")
    void testUniqueConstraint() {

        User user = buildUser();
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus);

        seatHoldRepository.save(buildHold(trip, user, 1, OffsetDateTime.now().plusMinutes(5)));

        assertThrows(Exception.class, () -> {
            seatHoldRepository.save(buildHold(trip, user, 1, OffsetDateTime.now().plusMinutes(5)));
            seatHoldRepository.flush();
        });
    }
}
