package com.ProyectoReservas.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Config {

    @Id
    @Column(length = 50)
    private String key;

    @Column(nullable = false, length = 200)
    private String value;
}

