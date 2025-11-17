package com.ProyectoReservas.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fare_rules", uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "from_stop_id", "to_stop_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FareRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Boolean dynamicPricing = false;
}

