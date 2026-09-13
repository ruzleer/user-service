package ru.aston.user.service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.EmailCheckException;
import ru.aston.user.service.exception.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceCreateUserTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User savedUser(Long id, String name, String email, int age) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }

    @Test
    @DisplayName("Все данные корректны — пользователь создаётся")
    void testCreateUserAllDataIsValid() {
        when(userDao.existByEmail("deni@mail.com")).thenReturn(false);
        when(userDao.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        User result = userService.createUser("Den", "deni@mail.com", 21);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Den");
        assertThat(result.getEmail()).isEqualTo("deni@mail.com");
        assertThat(result.getAge()).isEqualTo(21);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        User passedToDao = captor.getValue();
        assertThat(passedToDao.getName()).isEqualTo("Den");
        assertThat(passedToDao.getEmail()).isEqualTo("deni@mail.com");
        assertThat(passedToDao.getAge()).isEqualTo(21);

        verify(userDao).existByEmail("deni@mail.com");
    }

    @Test
    @DisplayName("Имя — пустая строка")
    void testNameIsEmpty() {
        assertThatThrownBy(() -> userService.createUser("", "deni@mail.com", 21))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Name");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Имя — null")
    void testNameIsNull() {
        assertThatThrownBy(() -> userService.createUser(null, "deni@mail.com", 21))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Name");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Имя с пробелами по краям")
    void testNameWithLeadingAndTrailingSpaces() {
        when(userDao.existByEmail("deni@mail.com")).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser("   Kali   ", "deni@mail.com", 21);

        assertThat(result.getName()).isEqualTo("Kali");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Kali");   // без пробелов
    }

    @Test
    @DisplayName("Возраст отрицательный")
    void testAgeIsNegative() {
        assertThatThrownBy(() -> userService.createUser("Kali", "deni@mail.com", -1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Age");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Возраст равен 0")
    void testAgeIsZero() {
        assertThatThrownBy(() -> userService.createUser("Deni", "deni@mail.com", 0))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Age");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Почта — пустая строка")
    void testEmailIsEmpty() {
        assertThatThrownBy(() -> userService.createUser("Deni", "", 21))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email");      // или "Email" — под ваш текст

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Почта — null")
    void testEmailIsNull() {
        assertThatThrownBy(() -> userService.createUser("Kali", null, 11))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Почта с пробелами по краям")
    void testEmailWithLeadingAndTrailingSpaces() {
        when(userDao.existByEmail("deni@mail.com")).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser("Denchik", "   deni@mail.com   ", 15);

        assertThat(result.getEmail()).isEqualTo("deni@mail.com");

        verify(userDao).existByEmail("deni@mail.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("deni@mail.com");
    }

    @Test
    @DisplayName("Почта неверного формата")
    void testEmailFormatIsInvalid() {
        assertThatThrownBy(() -> userService.createUser("Kalin", "that-an-email", 20))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email");

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Почта уже существует")
    void testEmailAlreadyExists() {
        when(userDao.existByEmail("deni@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("Kalin", "deni@mail.com", 21))
                .isInstanceOf(EmailCheckException.class)
                .hasMessageContaining("deni@mail.com");

        verify(userDao).existByEmail("deni@mail.com");
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Почта уже существует")
    void testEmailAlreadyExists_withSpaces() {
        when(userDao.existByEmail("deni@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("Kalin", "  deni@mail.com  ", 20))
                .isInstanceOf(EmailCheckException.class);

        verify(userDao).existByEmail("deni@mail.com");
        verify(userDao, never()).save(any());
    }

}
