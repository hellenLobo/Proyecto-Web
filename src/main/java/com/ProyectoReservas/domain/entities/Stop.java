package com.ProyectoReservas.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stops", uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "position"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer position;

    private BigDecimal lat;
    private BigDecimal lng;
}

