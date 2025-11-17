package com.ProyectoReservas.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "parcels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parcel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false, length = 120)
    private String senderName;

    @Column(length = 20)
    private String senderPhone;

    @Column(nullable = false, length = 120)
    private String receiverName;

    @Column(length = 20)
    private String receiverPhone;

    @ManyToOne
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;

    @ManyToOne
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParcelStatus status = ParcelStatus.CREATED;

    /*@Column(length = 300)
    private String proofPhotoUrl;

    @Column(length = 10)
    private String deliveryOtp;*/

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

