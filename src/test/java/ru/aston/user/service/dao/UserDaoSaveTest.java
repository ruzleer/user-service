package ru.aston.user.service.dao;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.user.service.AbstractPostgresTest.AbstractPostgresTest;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.DatabaseOperationException;

import static org.assertj.core.api.Assertions.*;

public class UserDaoSaveTest extends AbstractPostgresTest {
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

    private User buildValidUser() {
        User user = new User();
        user.setName("Den Kali");
        user.setEmail("den@mail.com");
        user.setAge(21);
        return user;
    }

    private User persistExistingUser() {
        User user = buildValidUser();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
        return user;
    }

    private long countUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(u) from User u", Long.class)
                    .getSingleResult();
        }
    }

    @Test
    @DisplayName("Пользователь успешно сохраняется в БД")
    void testSaveUserSuccessfully() {
        User user = buildValidUser();
        assertThat(user.getId()).isNull();

        User saved = userDao.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        try (Session session = sessionFactory.openSession()) {
            User fromDb = session.get(User.class, saved.getId());
            assertThat(fromDb.getName()).isEqualTo("Den Kali");
            assertThat(fromDb.getEmail()).isEqualTo("den@mail.com");
        }
    }

    @Test
    @DisplayName("Пользователь с существующим email не сохраняется")
    void testSaveUserWithDuplicateEmail() {
        persistExistingUser();

        User duplicate = buildValidUser();
        duplicate.setName("Anton");

        assertThatThrownBy(() -> userDao.save(duplicate))
                .isInstanceOf(DatabaseOperationException.class);

        assertThat(countUsers()).isEqualTo(1);
    }

    @Test
    @DisplayName("Пользователь с пустым именем не сохраняется")
    void testSaveUserWithEmptyName() {
        User user = buildValidUser();
        user.setName("");

        assertThatThrownBy(() -> userDao.save(user))
                .isInstanceOf(DatabaseOperationException.class);
        assertThat(countUsers()).isZero();
    }

    @Test
    @DisplayName("Пользователь с нулевым возрастом не сохраняется")
    void testSaveUserWithNullAge() {
        User user = buildValidUser();
        user.setAge(0);

        assertThatThrownBy(() -> userDao.save(user))
                .isInstanceOf(DatabaseOperationException.class);
        assertThat(countUsers()).isZero();
    }

    @Test
    @DisplayName("Пользователь с пустым email не сохраняется")
    void testSaveUserWithEmptyEmail() {
        User user = buildValidUser();
        user.setEmail("");

        assertThatThrownBy(() -> userDao.save(user))
                .isInstanceOf(DatabaseOperationException.class);
        assertThat(countUsers()).isZero();
    }

    @Test
    @DisplayName("Пользователь с отрицательным возрастом не сохраняется")
    void testSaveUserWithNegativeAge() {
        User user = buildValidUser();
        user.setAge(-5);

        assertThatThrownBy(() -> userDao.save(user))
                .isInstanceOf(DatabaseOperationException.class);
        assertThat(countUsers()).isZero();
    }

    @Test
    @DisplayName("Пользователь с некорректным email не сохраняется")
    void testSaveUserWithInvalidEmail() {
        User user = buildValidUser();
        user.setEmail("not-an-email");

        assertThatThrownBy(() -> userDao.save(user))
                .isInstanceOf(DatabaseOperationException.class);
        assertThat(countUsers()).isZero();
    }

}
