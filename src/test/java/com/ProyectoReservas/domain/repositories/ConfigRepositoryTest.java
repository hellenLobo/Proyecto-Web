package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ConfigRepository configRepository;

    @Test
    @DisplayName("Debe guardar y recuperar una configuración por clave")
    void testSaveAndFind() {
        Config config = Config.builder()
                .key("app.name")
                .value("Sistema de Reservas")
                .build();

        configRepository.save(config);

        Optional<Config> result = configRepository.findById("app.name");

        assertTrue(result.isPresent());
        assertEquals("Sistema de Reservas", result.get().getValue());
    }

    @Test
    @DisplayName("Debe actualizar correctamente una configuración existente")
    void testUpdate() {
        Config cfg = Config.builder()
                .key("max.seats")
                .value("40")
                .build();

        configRepository.save(cfg);

        // Actualizar
        cfg.setValue("45");
        configRepository.save(cfg);

        Config updated = configRepository.findById("max.seats").orElseThrow();
        assertEquals("45", updated.getValue());
    }

    @Test
    @DisplayName("Debe eliminar una configuración por clave")
    void testDelete() {
        Config cfg = Config.builder()
                .key("system.mode")
                .value("production")
                .build();

        configRepository.save(cfg);

        configRepository.deleteById("system.mode");

        assertFalse(configRepository.findById("system.mode").isPresent());
    }

    @Test
    @DisplayName("No debe permitir guardar una configuración con value null")
    void testValueNotNullConstraint() {
        Config cfg = Config.builder()
                .key("null.test")
                .value(null) // prohibited by schema
                .build();

        assertThrows(Exception.class, () ->
                configRepository.save(cfg)
        );
    }
}
