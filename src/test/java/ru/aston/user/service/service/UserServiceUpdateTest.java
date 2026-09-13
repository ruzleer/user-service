package ru.aston.user.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.DatabaseOperationException;
import ru.aston.user.service.exception.EmailCheckException;
import ru.aston.user.service.exception.ValidationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUpdateTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Alex");
        existingUser.setEmail("alex@mail.ru");
        existingUser.setAge(25);
    }

    @Test
    @DisplayName("Все данные корректны")
    void testUpdateUserAllDataIsValid() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.existByEmail("new@mail.ru")).thenReturn(false);
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "new@mail.ru", 30);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@mail.ru");
        assertThat(result.getAge()).isEqualTo(30);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(captor.capture());
        User passedToDao = captor.getValue();
        assertThat(passedToDao.getName()).isEqualTo("New Name");
        assertThat(passedToDao.getEmail()).isEqualTo("new@mail.ru");
        assertThat(passedToDao.getAge()).isEqualTo(30);

        verify(userDao).findById(1L);
        verify(userDao).existByEmail("new@mail.ru");
    }

    @Test
    @DisplayName("В имени пустая строка")
    void testNameIsEmpty() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, "", "new@mail.ru", 30))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Name");

        verify(userDao, never()).update(any());
        verify(userDao, never()).existByEmail(any());
    }

    @Test
    @DisplayName("Имя с пробелами по краям")
    void testNameWithLeadingAndTrailingSpaces() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.existByEmail("new@mail.ru")).thenReturn(false);
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, "   New Name   ", "new@mail.ru", 30);

        assertThat(result.getName()).isEqualTo("New Name");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Возраст меньше 0")
    void testAgeIsNegative() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, "New Name", "new@mail.ru", -1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Age");

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("В почте пустая строка")
    void testEmailIsEmpty() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, "New Name", "", 30))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email");

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Почта с пробелами по краям")
    void testEmailWithLeadingAndTrailingSpaces() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.existByEmail("new@mail.ru")).thenReturn(false);
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "   new@mail.ru   ", 30);

        assertThat(result.getEmail()).isEqualTo("new@mail.ru");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@mail.ru");
    }

    @Test
    @DisplayName("Почта неверный формат")
    void testEmailFormatIsInvalid() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, "New Name", "not-an-email", 30))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email");

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Почта уже существует у другого пользователя")
    void testEmailAlreadyExists() {
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.existByEmail("taken@mail.ru")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, "New Name", "taken@mail.ru", 30))
                .isInstanceOf(EmailCheckException.class)
                .hasMessageContaining("taken@mail.ru");

        verify(userDao, never()).update(any());
    }
}
