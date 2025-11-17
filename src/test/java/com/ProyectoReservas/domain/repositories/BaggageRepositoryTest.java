package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BaggageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private BaggageRepository baggageRepository;

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

    @Autowired
    private StopRepository stopRepository;

    // Helpers
    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-BAG")
                        .name("Ruta Bag")
                        .origin("A")
                        .destination("B")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(60)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("PLT-BAG")
                        .capacity(40)
                        .status(BusStatus.ACTIVE)
                        .build()
        );
    }

    private User buildPassenger() {
        return userRepository.save(
                User.builder()
                        .name("Juan")
                        .email("juan@example.com")
                        .passwordHash("123")
                        .role(Role.PASSENGER)
                        .status(UserStatus.ACTIVE)
                        .build()
        );
    }

    private Trip buildTrip(Route route, Bus bus) {
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(OffsetDateTime.now().plusHours(1))
                        .arrivalAt(OffsetDateTime.now().plusHours(3))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );
    }

    private Ticket buildTicket(Trip trip, User passenger) {
        return ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .passenger(passenger)
                        .seatNumber(5)
                        .price(BigDecimal.valueOf(45000))
                        .paymentMethod(PaymentMethod.CASH)
                        .status(TicketStatus.SOLD)
                        .purchasedAt(OffsetDateTime.now())
                        .build()
        );
    }

    private Baggage buildBaggage(Ticket ticket, String tag) {
        return baggageRepository.save(
                Baggage.builder()
                        .ticket(ticket)
                        .weightKg(BigDecimal.valueOf(12.5))
                        .fee(BigDecimal.valueOf(15000))
                        .tagCode(tag)
                        .build()
        );
    }

    // Tests

    @Test
    @DisplayName("Debe guardar y recuperar un equipaje")
    void testSaveAndRetrieve() {
        Route route = buildRoute();
        Bus bus = buildBus();
        User passenger = buildPassenger();
        Trip trip = buildTrip(route, bus);
        Ticket ticket = buildTicket(trip, passenger);

        Baggage baggage = buildBaggage(ticket, "TAG001");

        assertNotNull(baggage.getId());
        assertEquals("TAG001", baggage.getTagCode());
    }

    @Test
    @DisplayName("Debe encontrar equipaje por ticketId")
    void testFindByTicketId() {
        Route route = buildRoute();
        Bus bus = buildBus();
        User passenger = buildPassenger();
        Trip trip = buildTrip(route, bus);
        Ticket ticket = buildTicket(trip, passenger);

        buildBaggage(ticket, "TAG001");
        buildBaggage(ticket, "TAG002");

        List<Baggage> list = baggageRepository.findByTicketId(ticket.getId());

        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("No debe permitir dos equipajes con el mismo tagCode")
    void testUniqueTagCode() {
        Route route = buildRoute();
        Bus bus = buildBus();
        User passenger = buildPassenger();
        Trip trip = buildTrip(route, bus);
        Ticket ticket = buildTicket(trip, passenger);

        buildBaggage(ticket, "TAGUNI");

        assertThrows(Exception.class, () ->
                buildBaggage(ticket, "TAGUNI")
        );
    }
}
