package com.ProyectoReservas.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "incidents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Incident {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentEntityType entityType; // TRIP, TICKET, PARCEL

    @Column(nullable = false)
    private Long entityId; // ID de la entidad afectada

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentType type; // SECURITY, DELIVERY_FAIL, OVERBOOK, VEHICLE

    @Column(columnDefinition = "text")
    private String note;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

}

