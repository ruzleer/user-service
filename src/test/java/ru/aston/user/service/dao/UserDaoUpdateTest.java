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

public class UserDaoUpdateTest extends AbstractPostgresTest {
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

    private User persistUser(String name, String email, Integer age) {
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
    @DisplayName("Пользователь успешно обновляется в БД")
    void testUpdateUserSuccessfully() {
        User existing = persistUser("Old Name", "old@mail.ru", 25);

        existing.setName("New Name");
        existing.setEmail("new@mail.ru");
        existing.setAge(30);

        User updated = userDao.update(existing);

        assertThat(updated.getId()).isEqualTo(existing.getId());
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getEmail()).isEqualTo("new@mail.ru");
        assertThat(updated.getAge()).isEqualTo(30);

        try (Session session = sessionFactory.openSession()) {
            User fromDb = session.get(User.class, existing.getId());
            assertThat(fromDb.getName()).isEqualTo("New Name");
            assertThat(fromDb.getEmail()).isEqualTo("new@mail.ru");
        }
    }

    @Test
    @DisplayName("Обновление с уже существующей почтой")
    void testEmailAlreadyExists() {
        persistUser("First", "first@example.com", 20);
        User second = persistUser("Second", "second@example.com", 22);

        second.setEmail("first@example.com");

        assertThatThrownBy(() -> userDao.update(second))
                .isInstanceOf(DatabaseOperationException.class)
                .hasCauseInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Обновление с пустым именем")
    void testNameIsEmpty() {
        User user = persistUser("Alex", "alex@mail.ru", 30);
        user.setName("");

        assertThatThrownBy(() -> userDao.update(user))
                .isInstanceOf(DatabaseOperationException.class);
    }

    @Test
    @DisplayName("Обновление с пустой почтой")
    void testEmailIsEmpty() {
        User user = persistUser("Alex", "alex@mail.ru", 30);
        user.setEmail("");

        assertThatThrownBy(() -> userDao.update(user))
                .isInstanceOf(DatabaseOperationException.class);
    }

    @Test
    @DisplayName("Обновление с некорректным возрастом")
    void testAgeIsInvalid() {
        User user = persistUser("Alex", "alex@mail.ru", 30);
        user.setAge(-5);

        assertThatThrownBy(() -> userDao.update(user))
                .isInstanceOf(DatabaseOperationException.class);
    }

    @Test
    @DisplayName("Обновление с неправильным форматом почты")
    void testEmailFormatIsInvalid() {
        User user = persistUser("Alex", "alex@mail.ru", 30);
        user.setEmail("not-an-email");

        assertThatThrownBy(() -> userDao.update(user))
                .isInstanceOf(DatabaseOperationException.class);
    }
}
