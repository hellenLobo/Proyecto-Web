package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, String> {}

