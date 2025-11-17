package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParcelRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    // -------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------

    private Route buildRoute() {
        return routeRepository.save(
                Route.builder()
                        .code("RT-PA")
                        .name("Ruta Parcelas")
                        .origin("Origen X")
                        .destination("Destino Y")
                        .distanceKm(BigDecimal.valueOf(120))
                        .durationMin(150)
                        .build()
        );
    }

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("PAR-123")
                        .capacity(40)
                        .status(BusStatus.ACTIVE)
                        .amenities(null)
                        .build()
        );
    }

    private Trip buildTrip(Route route, Bus bus) {
        OffsetDateTime now = OffsetDateTime.now();
        return tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .departureAt(now.plusHours(1))
                        .arrivalAt(now.plusHours(4))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );
    }

    private Parcel buildParcel(Trip trip, String code, ParcelStatus status, OffsetDateTime createdAt) {
        return parcelRepository.save(
                Parcel.builder()
                        .code(code)
                        .trip(trip)
                        .senderName("Juan Perez")
                        .senderPhone("3001234567")
                        .receiverName("Maria Lopez")
                        .receiverPhone("3107894561")
                        .price(BigDecimal.valueOf(15000))
                        .status(status)
                        .createdAt(createdAt)
                        .build()
        );
    }

    // -------------------------------------------------------------
    // TEST 1: findByCode
    // -------------------------------------------------------------
    @Test
    @DisplayName("findByCode debe devolver la encomienda con el código indicado")
    void testFindByCode() {
        Trip trip = buildTrip(buildRoute(), buildBus());

        buildParcel(trip, "PKG001", ParcelStatus.CREATED, OffsetDateTime.now());

        var result = parcelRepository.findByCode("PKG001");

        assertTrue(result.isPresent());
        assertEquals("PKG001", result.get().getCode());
    }

    // -------------------------------------------------------------
    // TEST 2: findByStatus
    // -------------------------------------------------------------
    @Test
    @DisplayName("findByStatus debe devolver todas las encomiendas con un estado")
    void testFindByStatus() {
        Trip trip = buildTrip(buildRoute(), buildBus());

        buildParcel(trip, "PKG001", ParcelStatus.IN_TRANSIT, OffsetDateTime.now());
        buildParcel(trip, "PKG002", ParcelStatus.IN_TRANSIT, OffsetDateTime.now());

        List<Parcel> result = parcelRepository.findByStatus(ParcelStatus.IN_TRANSIT);

        assertEquals(2, result.size());
    }

    // -------------------------------------------------------------
    // TEST 3: findByStatusOrdered (JPQL)
    // -------------------------------------------------------------
    @Test
    @DisplayName("findByStatusOrdered debe devolver encomiendas ordenadas por fecha DESC")
    void testFindByStatusOrdered() {
        Trip trip = buildTrip(buildRoute(), buildBus());

        Parcel p1 = buildParcel(trip, "PKG001", ParcelStatus.CREATED, OffsetDateTime.now().minusHours(1));
        Parcel p2 = buildParcel(trip, "PKG002", ParcelStatus.CREATED, OffsetDateTime.now());

        List<Parcel> result = parcelRepository.findByStatusOrdered(ParcelStatus.CREATED);

        assertEquals(2, result.size());
        assertEquals("PKG002", result.get(0).getCode()); // más reciente primero
    }

    // -------------------------------------------------------------
    // TEST 4: UniqueConstraint (code)
    // -------------------------------------------------------------
    @Test
    @DisplayName("No debe permitir dos encomiendas con el mismo code")
    void testUniqueConstraint() {
        Trip trip = buildTrip(buildRoute(), buildBus());

        buildParcel(trip, "UNQ001", ParcelStatus.CREATED, OffsetDateTime.now());

        assertThrows(Exception.class, () -> {
            buildParcel(trip, "UNQ001", ParcelStatus.CREATED, OffsetDateTime.now());
            parcelRepository.flush();
        });
    }
}
