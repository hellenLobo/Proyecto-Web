package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Bus;
import com.ProyectoReservas.domain.entities.BusStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BusRepositoryTest extends BaseRepositoryTest {


    @Autowired
    private BusRepository busRepository;

    @Test
    @DisplayName("findByPlate debe encontrar un bus por su placa exacta")
    void findByPlate() {
        // Arrange
        Bus bus = Bus.builder()
                .plate("ABC123")
                .capacity(40)
                .amenities(Map.of("wifi", true))
                .status(BusStatus.ACTIVE)
                .build();

        busRepository.save(bus);

        // Act
        Optional<Bus> result = busRepository.findByPlate("ABC123");

        // Assert
        assertTrue(result.isPresent(), "Debe encontrar el bus por la placa");
        assertNotNull(result.get().getId(), "El bus guardado debe tener ID");
        assertEquals("ABC123", result.get().getPlate());
    }

    @Test
    @DisplayName("existsByPlateIgnoreCase debe validar existencia de placa ignorando mayúsculas/minúsculas")
    void existsByPlateIgnoreCase() {
        // Arrange
        Bus bus = Bus.builder()
                .plate("XYZ999")
                .capacity(50)
                .amenities(Map.of("wifi", true))
                .status(BusStatus.ACTIVE)
                .build();

        busRepository.save(bus);

        // Act & Assert
        assertTrue(
                busRepository.existsByPlateIgnoreCase("xyz999"),
                "Debe existir aunque la placa esté en minúsculas"
        );
        assertFalse(
                busRepository.existsByPlateIgnoreCase("NOEXISTE"),
                "No debe existir para placas no registradas"
        );
    }

    @Test
    @DisplayName("findByStatus debe devolver solo buses con el estado indicado")
    void findByStatus() {
        // Arrange
        Bus active1 = Bus.builder()
                .plate("BUS001")
                .capacity(30)
                .amenities(null)
                .status(BusStatus.ACTIVE)
                .build();

        Bus active2 = Bus.builder()
                .plate("BUS002")
                .capacity(35)
                .amenities(null)
                .status(BusStatus.ACTIVE)
                .build();

        Bus inactive = Bus.builder()
                .plate("BUS003")
                .capacity(40)
                .amenities(null)
                .status(BusStatus.INACTIVE)
                .build();

        busRepository.saveAll(List.of(active1, active2, inactive));

        // Act
        List<Bus> activeBuses = busRepository.findByStatus(BusStatus.ACTIVE);

        // Assert
        assertEquals(2, activeBuses.size(), "Debe haber exactamente 2 buses activos");
        assertTrue(
                activeBuses.stream().allMatch(b -> b.getStatus() == BusStatus.ACTIVE),
                "Todos los buses retornados deben ser ACTIVE"
        );
    }
}
