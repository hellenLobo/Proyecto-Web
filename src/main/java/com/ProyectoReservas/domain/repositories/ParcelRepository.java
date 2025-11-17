package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Parcel;
import com.ProyectoReservas.domain.entities.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Optional<Parcel> findByCode(String code);
    List<Parcel> findByStatus(ParcelStatus status);

    //Listar encomiendas en tránsito o entregadas.
    @Query("""
       SELECT p FROM Parcel p
       WHERE p.status = :status
       ORDER BY p.createdAt DESC
       """)
    List<Parcel> findByStatusOrdered(@Param("status") ParcelStatus status);

}

