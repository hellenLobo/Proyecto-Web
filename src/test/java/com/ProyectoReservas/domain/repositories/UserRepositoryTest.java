package com.ProyectoReservas.domain.repositories;

import com.ProyectoReservas.domain.entities.Role;
import com.ProyectoReservas.domain.entities.User;
import com.ProyectoReservas.domain.entities.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String name, String email, String phone, Role role, UserStatus status) {
        return User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .role(role)
                .status(status)
                .passwordHash("hash")
                .createdAt(OffsetDateTime.now())
                .build();
    }

    // ------------------ EMAIL ------------------

    @Test
    @DisplayName("findByEmailIgnoreCase debe encontrar usuario sin importar mayúsculas")
    void findByEmailIgnoreCase() {
        User u = buildUser("Juan", "juan@example.com", "3001", Role.PASSENGER, UserStatus.ACTIVE);
        userRepository.save(u);

        Optional<User> result = userRepository.findByEmailIgnoreCase("JUAN@EXAMPLE.COM");

        assertTrue(result.isPresent());
        assertEquals("juan@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("existsByEmailIgnoreCase debe detectar existencia ignorando mayúsculas")
    void existsByEmailIgnoreCase() {
        User u = buildUser("Ana", "ana@example.com", "3002", Role.PASSENGER, UserStatus.ACTIVE);
        userRepository.save(u);

        assertTrue(userRepository.existsByEmailIgnoreCase("ANA@EXAMPLE.COM"));
        assertFalse(userRepository.existsByEmailIgnoreCase("nope@example.com"));
    }

    // ------------------ PHONE ------------------

    @Test
    @DisplayName("findByPhone debe devolver usuario por teléfono exacto")
    void findByPhone() {
        User u = buildUser("Lucia", "lucia@example.com", "3003", Role.PASSENGER, UserStatus.ACTIVE);
        userRepository.save(u);

        Optional<User> result = userRepository.findByPhone("3003");

        assertTrue(result.isPresent());
        assertEquals("lucia@example.com", result.get().getEmail());
    }

    // ------------------ ROLE ------------------

    @Test
    @DisplayName("findByRole debe devolver todos los usuarios con un rol")
    void findByRole() {
        User a1 = buildUser("Admin1", "a1@example.com", "3004", Role.ADMIN, UserStatus.ACTIVE);
        User a2 = buildUser("Admin2", "a2@example.com", "3005", Role.ADMIN, UserStatus.ACTIVE);
        User p1 = buildUser("Passenger", "p1@example.com", "3006", Role.PASSENGER, UserStatus.ACTIVE);

        userRepository.saveAll(List.of(a1, a2, p1));

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        assertEquals(2, admins.size());
        assertTrue(admins.stream().allMatch(u -> u.getRole() == Role.ADMIN));
    }

    // ------------------ ACTIVE FILTERS ------------------

    @Test
    @DisplayName("findActiveUsers debe devolver solo usuarios ACTIVE")
    void findActiveUsers() {
        User u1 = buildUser("A1", "a1@ex.com", "3007", Role.PASSENGER, UserStatus.ACTIVE);
        User u2 = buildUser("A2", "a2@ex.com", "3008", Role.PASSENGER, UserStatus.ACTIVE);
        User u3 = buildUser("I1", "i1@ex.com", "3009", Role.PASSENGER, UserStatus.INACTIVE);

        userRepository.saveAll(List.of(u1, u2, u3));

        List<User> active = userRepository.findActiveUsers();

        assertEquals(2, active.size());
        assertTrue(active.stream().allMatch(u -> u.getStatus() == UserStatus.ACTIVE));
    }

    @Test
    @DisplayName("findActiveDrivers debe devolver solo conductores activos")
    void findActiveDrivers() {
        User d1 = buildUser("Driver1", "d1@ex.com", "3010", Role.DRIVER, UserStatus.ACTIVE);
        User d2 = buildUser("Driver2", "d2@ex.com", "3011", Role.DRIVER, UserStatus.ACTIVE);
        User d3 = buildUser("DriverInactive", "d3@ex.com", "3012", Role.DRIVER, UserStatus.INACTIVE);
        User p = buildUser("Passenger", "p@ex.com", "3013", Role.PASSENGER, UserStatus.ACTIVE);

        userRepository.saveAll(List.of(d1, d2, d3, p));

        List<User> drivers = userRepository.findActiveDrivers();

        assertEquals(2, drivers.size());
        assertTrue(drivers.stream().allMatch(
                u -> u.getRole() == Role.DRIVER && u.getStatus() == UserStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("findActiveClerks debe devolver solo taquilleros activos")
    void findActiveClerks() {
        User c1 = buildUser("Clerk1", "c1@ex.com", "3014", Role.CLERK, UserStatus.ACTIVE);
        User c2 = buildUser("Clerk2", "c2@ex.com", "3015", Role.CLERK, UserStatus.ACTIVE);
        User c3 = buildUser("ClerkInactive", "c3@ex.com", "3016", Role.CLERK, UserStatus.INACTIVE);

        userRepository.saveAll(List.of(c1, c2, c3));

        List<User> clerks = userRepository.findActiveClerks();

        assertEquals(2, clerks.size());
    }

    @Test
    @DisplayName("findActiveDispatchers debe devolver solo despachadores activos")
    void findActiveDispatchers() {
        User d1 = buildUser("Disp1", "dp1@ex.com", "3017", Role.DISPATCHER, UserStatus.ACTIVE);
        User d2 = buildUser("Disp2", "dp2@ex.com", "3018", Role.DISPATCHER, UserStatus.ACTIVE);
        User d3 = buildUser("DispInactive", "dp3@ex.com", "3019", Role.DISPATCHER, UserStatus.INACTIVE);

        userRepository.saveAll(List.of(d1, d2, d3));

        List<User> dispatchers = userRepository.findActiveDispatchers();

        assertEquals(2, dispatchers.size());
    }

    // ------------------ SEARCH USERS ------------------

    @Test
    @DisplayName("searchUsers debe buscar por nombre, email o teléfono")
    void searchUsers() {
        User u1 = buildUser("Carlos Ruiz", "carlos@ex.com", "3100000000", Role.PASSENGER, UserStatus.ACTIVE);
        User u2 = buildUser("Ana María", "ana@ex.com", "3101111111", Role.CLERK, UserStatus.ACTIVE);
        User u3 = buildUser("Pepito", "pepito@ex.com", "3112222222", Role.DRIVER, UserStatus.ACTIVE);

        userRepository.saveAll(List.of(u1, u2, u3));

        List<User> result1 = userRepository.searchUsers("ana");
        List<User> result2 = userRepository.searchUsers("3101");
        List<User> result3 = userRepository.searchUsers("carlos");

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(1, result3.size());
    }
}
