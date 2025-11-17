package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Bus;
import com.ProyectoReservas.domain.entities.Seat;
import com.ProyectoReservas.domain.entities.SeatType;
import com.ProyectoReservas.domain.entities.BusStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SeatRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BusRepository busRepository;

    private Bus buildBus() {
        return busRepository.save(
                Bus.builder()
                        .plate("XYZ123")
                        .capacity(40)
                        .amenities(null)
                        .status(BusStatus.ACTIVE)
                        .build()
        );
    }

    private Seat buildSeat(Bus bus, int number) {
        return Seat.builder()
                .bus(bus)
                .number(number)
                .type(SeatType.STANDARD)
                .build();
    }

    // ------------------------------------------------------------------------
    // MÉTODO 1: findByBusId
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("findByBusId debe retornar todas las sillas del bus")
    void testFindByBusId() {
        Bus bus = buildBus();

        Seat s1 = seatRepository.save(buildSeat(bus, 1));
        Seat s2 = seatRepository.save(buildSeat(bus, 2));

        List<Seat> result = seatRepository.findByBusId(bus.getId());

        assertEquals(2, result.size(), "Debe devolver las sillas del bus");
        assertTrue(result.stream().anyMatch(s -> s.getNumber() == 1));
        assertTrue(result.stream().anyMatch(s -> s.getNumber() == 2));
    }

    // ------------------------------------------------------------------------
    // MÉTODO 2: existsByBusIdAndNumber
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("existsByBusIdAndNumber debe validar si una silla existe")
    void testExistsByBusIdAndNumber() {
        Bus bus = buildBus();

        seatRepository.save(buildSeat(bus, 5));

        assertTrue(seatRepository.existsByBusIdAndNumber(bus.getId(), 5));
        assertFalse(seatRepository.existsByBusIdAndNumber(bus.getId(), 99));
    }

    // ------------------------------------------------------------------------
    // MÉTODO 3: findByBusIdAndNumber
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("findByBusIdAndNumber debe retornar una silla específica")
    void testFindByBusIdAndNumber() {
        Bus bus = buildBus();

        seatRepository.save(buildSeat(bus, 10));

        Optional<Seat> result = seatRepository.findByBusIdAndNumber(bus.getId(), 10);

        assertTrue(result.isPresent(), "Debe encontrar la silla número 10");
        assertEquals(10, result.get().getNumber());
    }

    // ------------------------------------------------------------------------
    // EXTRA: Validación de unicidad (bus_id, number)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("No debe permitir duplicar número de silla en el mismo bus")
    void testUniqueConstraint() {
        Bus bus = buildBus();

        seatRepository.save(buildSeat(bus, 1));

        assertThrows(Exception.class, () -> {
            seatRepository.save(buildSeat(bus, 1)); // Intento duplicar
            seatRepository.flush(); // fuerza escritura en DB
        });
    }
}
