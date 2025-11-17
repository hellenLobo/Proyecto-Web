package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.*;
import com.ProyectoReservas.domain.entities.Role;
import com.ProyectoReservas.domain.entities.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private User buildUser(String email) {
        return userRepository.save(
                User.builder()
                        .name("UserTest")
                        .email(email)
                        .phone("3000000000")
                        .role(Role.PASSENGER)
                        .status(UserStatus.ACTIVE)
                        .passwordHash("1234")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-TK")
                        .name("Ruta Tickets")
                        .origin("City X")
                        .destination("City Y")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(120)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("PLT-123")
                        .capacity(40)
                        .status(BusStatus.ACTIVE)
                        .amenities(null)
                        .build()
        );
    }

    private Trip buildTrip(Route route, Bus bus, OffsetDateTime departure) {
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(departure)
                        .arrivalAt(departure.plusHours(2))
                        .status(TripStatus.SCHEDULED)
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
    // TEST 1: findByTripId (PAGEABLE)
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByTripId debe devolver tickets paginados por trip")
    void testFindByTripId() {
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(1));
        User user = buildUser("user1@test.com");

        buildTicket(trip, user, 1);
        buildTicket(trip, user, 2);
        buildTicket(trip, user, 3);

        Page<Ticket> page = ticketRepository.findByTripId(
                trip.getId(), PageRequest.of(0, 2)
        );

        assertEquals(2, page.getContent().size(), "Debe devolver 2 tickets en página 1");
        assertEquals(3, page.getTotalElements(), "Total debe ser 3");
    }

    // ---------------------------------------------------------
    // TEST 2: findByPassengerId
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByPassengerId debe devolver todos los tickets de un pasajero")
    void testFindByPassengerId() {
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(1));
        User u1 = buildUser("userA@test.com");
        User u2 = buildUser("userB@test.com");

        buildTicket(trip, u1, 1);
        buildTicket(trip, u1, 2);
        buildTicket(trip, u2, 3);

        List<Ticket> tickets = ticketRepository.findByPassengerId(u1.getId());

        assertEquals(2, tickets.size(), "El pasajero A debe tener 2 tickets");
    }

    // ---------------------------------------------------------
    // TEST 3: findByStatus
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByStatus debe devolver tickets con el estado especificado")
    void testFindByStatus() {
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(1));
        User user = buildUser("user@test.com");

        buildTicket(trip, user, 1);
        Ticket cancelled = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .passenger(user)
                        .seatNumber(2)
                        .price(BigDecimal.valueOf(50000))
                        .paymentMethod(PaymentMethod.CASH)
                        .status(TicketStatus.CANCELLED)
                        .purchasedAt(OffsetDateTime.now())
                        .build()
        );

        List<Ticket> cancelledList = ticketRepository.findByStatus(TicketStatus.CANCELLED);

        assertEquals(1, cancelledList.size());
        assertEquals(TicketStatus.CANCELLED, cancelledList.get(0).getStatus());
    }

    // ---------------------------------------------------------
    // TEST 4: findSoldTicketsByTrip (JPQL)
    // ---------------------------------------------------------
    @Test
    @DisplayName("findSoldTicketsByTrip debe retornar solo tickets SOLD ordenados por asiento")
    void testFindSoldTicketsByTrip() {
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(1));
        User user = buildUser("user@test.com");

        buildTicket(trip, user, 3);
        buildTicket(trip, user, 1);
        buildTicket(trip, user, 2);

        List<Ticket> result = ticketRepository.findSoldTicketsByTrip(trip.getId());

        assertEquals(3, result.size(), "Debe traer 3 tickets vendidos");
        assertEquals(1, result.get(0).getSeatNumber());
        assertEquals(2, result.get(1).getSeatNumber());
        assertEquals(3, result.get(2).getSeatNumber());
    }

    // ---------------------------------------------------------
    // TEST 5: findTicketsByPassengerAndDateRange (JPQL)
    // ---------------------------------------------------------
    @Test
    @DisplayName("findTicketsByPassengerAndDateRange debe filtrar por fecha y pasajero")
    void testFindTicketsByPassengerAndDateRange() {
        Route route = buildRoute();
        Bus bus = buildBus();
        User user = buildUser("range@test.com");

        Trip withinRange = buildTrip(route, bus, OffsetDateTime.now().plusDays(1));
        Trip outsideRange = buildTrip(route, bus, OffsetDateTime.now().plusDays(10));

        buildTicket(withinRange, user, 1);
        buildTicket(outsideRange, user, 2);

        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now().plusDays(5);

        List<Ticket> result = ticketRepository.findTicketsByPassengerAndDateRange(
                user.getId(), start, end
        );

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getSeatNumber());
    }

    // ---------------------------------------------------------
    // TEST 6: UniqueConstraint (trip_id, seat_number)
    // ---------------------------------------------------------
    @Test
    @DisplayName("Debe fallar al tratar de vender dos tickets para la misma silla en un trip")
    void testUniqueConstraint() {
        Route route = buildRoute();
        Bus bus = buildBus();
        Trip trip = buildTrip(route, bus, OffsetDateTime.now().plusHours(1));
        User user = buildUser("unique@test.com");

        buildTicket(trip, user, 1);

        assertThrows(Exception.class, () -> {
            buildTicket(trip, user, 1); // duplicado
            ticketRepository.flush();  // fuerza escritura
        });
    }
}
