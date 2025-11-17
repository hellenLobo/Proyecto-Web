package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Incident;
import com.ProyectoReservas.domain.entities.IncidentType;
import com.ProyectoReservas.domain.entities.IncidentEntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IncidentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    // Helper para crear incidentes
    private Incident buildIncident(
            IncidentType type,
            IncidentEntityType entityType,
            Long entityId,
            OffsetDateTime createdAt
    ) {
        return incidentRepository.save(
                Incident.builder()
                        .type(type)
                        .entityType(entityType)
                        .entityId(entityId)
                        .note("Test note")
                        .createdAt(createdAt)
                        .build()
        );
    }

    @Test
    @DisplayName("Debe guardar y recuperar un incidente")
    void testSaveAndRetrieve() {
        Incident incident = buildIncident(
                IncidentType.SECURITY,
                IncidentEntityType.TICKET,
                10L,
                OffsetDateTime.now()
        );

        assertNotNull(incident.getId());

        Incident found = incidentRepository.findById(incident.getId()).orElseThrow();
        assertEquals(IncidentType.SECURITY, found.getType());
        assertEquals(IncidentEntityType.TICKET, found.getEntityType());
    }

    @Test
    @DisplayName("Debe encontrar incidentes por tipo")
    void testFindByType() {
        buildIncident(IncidentType.SECURITY, IncidentEntityType.TICKET, 1L, OffsetDateTime.now());
        buildIncident(IncidentType.SECURITY, IncidentEntityType.TRIP, 2L, OffsetDateTime.now());
        buildIncident(IncidentType.DELIVERY_FAIL, IncidentEntityType.PARCEL, 3L, OffsetDateTime.now());

        List<Incident> list = incidentRepository.findByType(IncidentType.SECURITY);

        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(i -> i.getType() == IncidentType.SECURITY));
    }

    @Test
    @DisplayName("Debe buscar incidentes por tipo y rango de fechas")
    void testFindByTypeAndDateRange() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);
        OffsetDateTime twoDaysAgo = now.minusDays(2);

        // dentro del rango
        Incident i1 = buildIncident(IncidentType.VEHICLE, IncidentEntityType.TRIP, 1L, yesterday);
        Incident i2 = buildIncident(IncidentType.VEHICLE, IncidentEntityType.TICKET, 2L, now.minusHours(2));

        // fuera del rango
        buildIncident(IncidentType.VEHICLE, IncidentEntityType.PARCEL, 3L, twoDaysAgo.minusHours(5));

        List<Incident> results = incidentRepository.findByTypeAndDateRange(
                IncidentType.VEHICLE,
                twoDaysAgo,
                now
        );

        assertEquals(2, results.size());

        // Validar orden descendente (más reciente primero)
        assertTrue(results.get(0).getCreatedAt().isAfter(results.get(1).getCreatedAt()));
    }

    @Test
    @DisplayName("No debe permitir entityType null (constraint NOT NULL)")
    void testEntityTypeNotNull() {
        Incident incident = Incident.builder()
                .type(IncidentType.OVERBOOK)
                .entityId(5L)
                .createdAt(OffsetDateTime.now())
                .build();

        assertThrows(Exception.class, () -> incidentRepository.save(incident));
    }

    @Test
    @DisplayName("No debe permitir type null (constraint NOT NULL)")
    void testIncidentTypeNotNull() {
        Incident incident = Incident.builder()
                .entityType(IncidentEntityType.TICKET)
                .entityId(5L)
                .createdAt(OffsetDateTime.now())
                .build();

        assertThrows(Exception.class, () -> incidentRepository.save(incident));
    }
}
