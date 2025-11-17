package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Role;
import com.ProyectoReservas.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // --- Autenticación y validaciones ---
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhone(String phone);

    // --- Rol específico ---
    List<User> findByRole(Role role);

    // Conductores
    @Query("SELECT u FROM User u WHERE u.role = 'DRIVER' AND u.status = 'ACTIVE'")
    List<User> findActiveDrivers();

    // Taquilleros
    @Query("SELECT u FROM User u WHERE u.role = 'CLERK' AND u.status = 'ACTIVE'")
    List<User> findActiveClerks();

    // Despachadores
    @Query("SELECT u FROM User u WHERE u.role = 'DISPATCHER' AND u.status = 'ACTIVE'")
    List<User> findActiveDispatchers();

    // --- Filtrar solo usuarios activos ---
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    // --- Búsqueda por nombre (útil para Admin) ---
    @Query("""
        SELECT u 
        FROM User u 
        WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
           OR u.phone LIKE CONCAT('%', :query, '%')
        """)
    List<User> searchUsers(String query);

}
