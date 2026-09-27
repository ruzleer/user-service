package ru.aston.user.service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.aston.user.service.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        User user = new User(25, "crazyfrog", "crazyfrog@mail.com", null);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User(25, "crazyfrog", "crazyfrog@mail.com", null);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("crazyfrog@mail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("crazyfrog", foundUser.get().getName());
    }

    @Test
    void shouldCheckUserExistsByEmail() {
        User user = new User(25, "crazyfrog", "crazyfrog@mail.com", null);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("crazyfrog@mail.com"));
        assertFalse(userRepository.existsByEmail("other@mail.com"));
    }

    @Test
    void shouldFindUsersByNameIgnoreCase() {
        userRepository.save(
                new User(25, "crazyfrog", "crazyfrog@mail.com", null)
        );

        userRepository.save(
                new User(30, "CrazyFrog", "other@mail.com", null)
        );

        List<User> users =
                userRepository.findByNameContainingIgnoreCase("crazyfrog");

        assertEquals(2, users.size());
    }
}