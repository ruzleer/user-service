package ru.aston.user.service.dao;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.user.service.AbstractPostgresTest;
import ru.aston.user.service.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UserDaoImplTest extends AbstractPostgresTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private User persistUser(String name, String email, int age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }

        return user;
    }

    @Test
    @DisplayName("delete(Long id) — пользователь удалён")
    void shouldDeleteUser() {
        User user = persistUser(
                "Ivan",
                "ivan@example.com",
                25
        );

        userDao.delete(user.getId());

        Optional<User> result = userDao.findById(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById(Long id) — пользователь найден")
    void shouldFindUserById() {
        User user = persistUser(
                "Ivan",
                "ivan@example.com",
                25
        );

        Optional<User> result = userDao.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getName()).isEqualTo("Ivan");
        assertThat(result.get().getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    @DisplayName("findById(Long id) — пользователь не найден")
    void shouldReturnEmptyWhenUserNotFound() {

        Optional<User> result = userDao.findById(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll() — возвращаются все пользователи")
    void shouldFindAllUsers() {
        persistUser("Ivan", "ivan@example.com", 25);
        persistUser("Petr", "petr@example.com", 30);
        persistUser("Anna", "anna@example.com", 22);

        List<User> result = userDao.findAll();

        assertThat(result)
                .hasSize(3)
                .extracting(User::getEmail)
                .containsExactly(
                        "ivan@example.com",
                        "petr@example.com",
                        "anna@example.com"
                );
    }

    @Test
    @DisplayName("existByEmail(String email) — email существует")
    void shouldReturnTrueWhenEmailExists() {
        persistUser(
                "Ivan",
                "ivan@example.com",
                25
        );

        boolean result = userDao.existByEmail("ivan@example.com");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existByEmail(String email) — email не существует")
    void shouldReturnFalseWhenEmailDoesNotExist() {

        boolean result = userDao.existByEmail("unknown@example.com");

        assertThat(result).isFalse();
    }
}