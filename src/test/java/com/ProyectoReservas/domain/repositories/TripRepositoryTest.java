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

public class TripRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT100")
                        .name("Ruta Test")
                        .origin("City A")
                        .destination("City B")
                        .distanceKm(BigDecimal.valueOf(150))
                        .durationMin(180)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("BUS100")
                        .capacity(40)
                        .amenities(null)
                        .status(BusStatus.ACTIVE)
                        .build()
        );
    }

    private Trip buildTrip(Route route, Bus bus, OffsetDateTime departureAt, TripStatus status) {
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(departureAt)
                        .arrivalAt(departureAt.plusHours(3))
                        .status(status)
                        .build()
        );
    }

    private User buildUser() {
        return userRepository.save(
                User.builder()
                        .name("Passenger")
                        .email("user@test.com")
                        .phone("3001234567")
                        .role(Role.PASSENGER)
                        .status(UserStatus.ACTIVE)
                        .passwordHash("hash")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    private Ticket buildTicket(Trip trip, User user, int seatNumber) {
        return ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .passenger(user)
                        .seatNumber(seatNumber)
                        .price(BigDecimal.valueOf(50000))
                        .paymentMethod(PaymentMethod.CASH)
                        .status(TicketStatus.SOLD)
                        .purchasedAt(OffsetDateTime.now())
                        .build()
        );
    }


    // ---------------------------------------------------------
    // TEST 1: findByRouteId
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByRouteId debe devolver los viajes asociados a la ruta")
    void testFindByRouteId() {
        Route route = buildRoute();
        Bus bus = buildBus();

        Trip t1 = buildTrip(route, bus, OffsetDateTime.now().plusHours(1), TripStatus.SCHEDULED);
        Trip t2 = buildTrip(route, bus, OffsetDateTime.now().plusHours(5), TripStatus.SCHEDULED);

        List<Trip> result = tripRepository.findByRouteId(route.getId());

        assertEquals(2, result.size());
    }

    // ---------------------------------------------------------
    // TEST 2: findByStatus
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByStatus debe devolver los viajes con un estado específico")
    void testFindByStatus() {
        Route route = buildRoute();
        Bus bus = buildBus();

        buildTrip(route, bus, OffsetDateTime.now().plusHours(1), TripStatus.SCHEDULED);
        buildTrip(route, bus, OffsetDateTime.now().plusHours(2), TripStatus.CANCELLED);

        List<Trip> result = tripRepository.findByStatus(TripStatus.SCHEDULED);

        assertEquals(1, result.size());
        assertEquals(TripStatus.SCHEDULED, result.get(0).getStatus());
    }

    // ---------------------------------------------------------
    // TEST 3: findAvailableTrips (JPQL)
    // ---------------------------------------------------------
    @Test
    @DisplayName("findAvailableTrips debe devolver los viajes programados en la fecha indicada")
    void testFindAvailableTrips() {
        Route route = buildRoute();
        Bus bus = buildBus();

        OffsetDateTime now = OffsetDateTime.now();

        // VIAJE DENTRO DEL DÍA
        Trip t1 = buildTrip(route, bus, now.withHour(10), TripStatus.SCHEDULED);

        // VIAJE OTRO DÍA
        buildTrip(route, bus, now.plusDays(1).withHour(10), TripStatus.SCHEDULED);

        List<Trip> result = tripRepository.findAvailableTrips(route.getId(), now);

        assertEquals(1, result.size());
        assertEquals(t1.getId(), result.get(0).getId());
    }

    // ---------------------------------------------------------
    // TEST 4: countOccupiedSeats (JPQL COUNT)
    // ---------------------------------------------------------
    @Test
    @DisplayName("countOccupiedSeats debe contar correctamente las sillas vendidas")
    void testCountOccupiedSeats() {
        Route route = buildRoute();
        Bus bus = buildBus();
        User user = buildUser();

        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(2), TripStatus.SCHEDULED);

        buildTicket(trip, user, 1);
        buildTicket(trip, user, 2);
        buildTicket(trip, user, 3);

        long count = tripRepository.countOccupiedSeats(trip.getId());

        assertEquals(3, count);
    }
}
