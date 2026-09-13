package ru.aston.user.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userDao);
    }

    @Test
    void createUser_shouldCreateUser_whenDataIsValid() {
        User savedUser = new User();
        savedUser.setName("Ivan");
        savedUser.setEmail("ivan@mail.com");
        savedUser.setAge(25);

        when(userDao.existByEmail("ivan@mail.com"))
                .thenReturn(false);

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        User user = userService.createUser(
                "Ivan",
                "ivan@mail.com",
                25
        );

        assertEquals("Ivan", user.getName());
        assertEquals("ivan@mail.com", user.getEmail());
    }

    @Test
    void createUser_shouldThrowException_whenNameIsEmpty() {
        assertThrows(
                ValidationException.class,
                () -> userService.createUser(
                        "",
                        "test@test.com",
                        25
                )
        );

        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowException_whenAgeIsNegative() {
        assertThrows(
                ValidationException.class,
                () -> userService.createUser(
                        "Alex",
                        "test@test.com",
                        -5
                )
        );

        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_shouldTrimName_whenNameContainsSpaces() {
        User savedUser = new User();
        savedUser.setName("Ivan");

        when(userDao.existByEmail("ivan@mail.com"))
                .thenReturn(false);

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        User user = userService.createUser(
                "  Ivan  ",
                "ivan@mail.com",
                25
        );

        assertEquals("Ivan", user.getName());
    }

    @Test
    void createUser_shouldThrowException_whenEmailIsEmpty() {
        assertThrows(
                ValidationException.class,
                () -> userService.createUser(
                        "Ivan",
                        "",
                        25
                )
        );

        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_shouldTrimEmail_whenEmailContainsSpaces() {
        User savedUser = new User();
        savedUser.setEmail("ivan@mail.com");

        when(userDao.existByEmail("ivan@mail.com"))
                .thenReturn(false);

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        User user = userService.createUser(
                "Ivan",
                "  ivan@mail.com  ",
                25
        );

        assertEquals("ivan@mail.com", user.getEmail());
    }

    @Test
    void createUser_shouldThrowException_whenEmailFormatIsInvalid() {
        assertThrows(
                ValidationException.class,
                () -> userService.createUser(
                        "Ivan",
                        "ivanmail.com",
                        25
                )
        );

        verifyNoInteractions(userDao);
    }

    @Test
    void findById_shouldThrowException_whenIdIsNegative() {
        assertThrows(
                ValidationException.class,
                () -> userService.findById(-1L)
        );

        verifyNoInteractions(userDao);
    }
}

