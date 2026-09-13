package ru.aston.user.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.user.service.exception.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Test
    void TestValidateName() {
        String name = "";
        String email = "alex@mail.ru";
        int age = 25;

        assertThatThrownBy(() -> userService.createUser(name, email, age))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Name cannot be empty");
    }

    @Test
    void TestValidateEmail() {
        String name = "Alex";
        String email = "invalid-email";
        int age = 25;

        assertThatThrownBy(() -> userService.createUser(name, email, age))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    void TestValidateAge() {
        String name = "Alex";
        String email = "alex@mail.ru";
        int age = -5;

        assertThatThrownBy(() -> userService.createUser(name, email, age))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Age must be positive");
    }

}

