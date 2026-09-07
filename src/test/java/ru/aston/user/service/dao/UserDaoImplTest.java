package ru.aston.user.service.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exceptions.UserNotSaveException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserDaoImplTest {

    private UserDaoImpl userDao;

    private SessionFactory sessionFactory;

    User user1 = new User(25, "Alex Ron", "alex@mail.ru", LocalDateTime.now());

    @BeforeEach
    void setUp() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userDao = new UserDaoImpl();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


    @Test
    void TestSaveUser() {
        User savedUser = userDao.save(user1);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Alex Ron");
        assertThat(savedUser.getEmail()).isEqualTo("alex@mail.ru");

        userDao.delete(savedUser.getId());
    }

    @Test
    void TestUpdateUser() {
        User savedUser = userDao.save(user1);

        savedUser.setName("Alex Ton");
        savedUser.setAge(30);

        User updatedUser = userDao.update(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Alex Ton");
        assertThat(updatedUser.getAge()).isEqualTo(30);

        userDao.delete(savedUser.getId());
    }

    @Test
    void TestFindById() {
        User savedUser = userDao.save(user1);

        Optional<User> found = userDao.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alex Ron");

        userDao.delete(savedUser.getId());
    }

    @Test
    void TestDeleteUserByID() {
        User savedUser = userDao.save(user1);

        userDao.delete(savedUser.getId());

        Optional<User> found = userDao.findById(savedUser.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void TestSearchByEmail() {
        User savedUser = userDao.save(user1);

        boolean exists = userDao.existByEmail("alex@mail.ru");

        assertThat(exists).isTrue();

        userDao.delete(savedUser.getId());
    }


}
