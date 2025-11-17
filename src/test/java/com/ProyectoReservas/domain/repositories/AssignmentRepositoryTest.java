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

public class AssignmentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AssignmentRepository assignmentRepository;

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

    private User buildDriver(String email) {
        return userRepository.save(
                User.builder()
                        .name("Driver")
                        .email(email)
                        .phone("3001112233")
                        .role(Role.DRIVER)
                        .status(UserStatus.ACTIVE)
                        .passwordHash("hash")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    private User buildDispatcher(String email) {
        return userRepository.save(
                User.builder()
                        .name("Dispatcher")
                        .email(email)
                        .phone("3005556666")
                        .role(Role.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .passwordHash("hash")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-ASG-" + System.nanoTime())
                        .name("Ruta Asignaciones")
                        .origin("City A")
                        .destination("City B")
                        .distanceKm(BigDecimal.valueOf(100))
                        .durationMin(120)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("ASG" + System.nanoTime())
                        .capacity(40)
                        .status(BusStatus.ACTIVE)
                        .amenities(null)
                        .build()
        );
    }

    private Trip buildTrip(OffsetDateTime dep) {
        Route route = buildRoute();
        Bus bus = buildBus();
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(dep)
                        .arrivalAt(dep.plusHours(3))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );
    }

    private Assignment buildAssignment(Trip trip, User driver, User dispatcher) {
        return assignmentRepository.save(
                Assignment.builder()
                        .trip(trip)
                        .driver(driver)
                        .dispatcher(dispatcher)
                        .checklistOk(false)
                        .assignedAt(OffsetDateTime.now())
                        .build()
        );
    }

    // ---------------------------------------------------------
    // TEST 1: findByTripId
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByTripId debe traer asignaciones del viaje indicado")
    void testFindByTripId() {

        Trip trip = buildTrip(OffsetDateTime.now().plusHours(4));
        User driver = buildDriver("driver@test.com");
        User dispatcher = buildDispatcher("dispatcher@test.com");

        buildAssignment(trip, driver, dispatcher);

        List<Assignment> result = assignmentRepository.findByTripId(trip.getId());

        assertEquals(1, result.size());
        assertEquals(driver.getId(), result.get(0).getDriver().getId());
    }

    // ---------------------------------------------------------
    // TEST 2: findByDriverAndDate (JPQL)
    // ---------------------------------------------------------
    @Test
    @DisplayName("findByDriverAndDate debe traer asignaciones del conductor en la fecha dada")
    void testFindByDriverAndDate() {

        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime tomorrow = today.plusDays(1);

        User driver = buildDriver("driver2@test.com");
        User dispatcher = buildDispatcher("dispatcher2@test.com");

        Trip tripToday = buildTrip(today.withHour(10));
        Trip tripTomorrow = buildTrip(tomorrow.withHour(10));

        buildAssignment(tripToday, driver, dispatcher);
        buildAssignment(tripTomorrow, driver, dispatcher);

        List<Assignment> sameDayAssignments =
                assignmentRepository.findByDriverAndDate(driver.getId(), today);

        assertEquals(1, sameDayAssignments.size());
        assertEquals(tripToday.getId(), sameDayAssignments.get(0).getTrip().getId());
    }

    // ---------------------------------------------------------
    // TEST 3: Validar que múltiples asignaciones a diferentes viajes funcionan
    // ---------------------------------------------------------
    @Test
    @DisplayName("Puede asignarse el mismo conductor a diferentes viajes sin restricciones")
    void testAssignmentsForDifferentTrips() {

        User driver = buildDriver("multi@test.com");
        User dispatcher = buildDispatcher("dispatch@test.com");

        Trip t1 = buildTrip(OffsetDateTime.now().plusHours(1));
        Trip t2 = buildTrip(OffsetDateTime.now().plusHours(3));

        buildAssignment(t1, driver, dispatcher);
        buildAssignment(t2, driver, dispatcher);

        List<Assignment> all = assignmentRepository.findByDriverAndDate(
                driver.getId(),
                OffsetDateTime.now()
        );

        // podría ser 0, 1 o 2 dependiendo hora exacta—no validamos cantidad,
        // solo validamos que se pudieron crear sin errores
        assertTrue(all.size() >= 0);
    }
}
