package ru.aston.user.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.user.service.dto.UserCreateDto;
import ru.aston.user.service.dto.UserResponseDto;
import ru.aston.user.service.dto.UserUpdateDto;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.EmailAlreadyExistsException;
import ru.aston.user.service.exception.UserNotFoundException;
import ru.aston.user.service.mapper.UserMapper;
import ru.aston.user.service.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto responseDto;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Memchik")
                .email("dumala@sova.com")
                .age(20)
                .createdAt(LocalDateTime.now())
                .build();

        responseDto = UserResponseDto.builder()
                .id(1L)
                .name("Memchik")
                .email("dumala@sova.com")
                .age(20)
                .createdAt(user.getCreatedAt())
                .build();

        createDto = UserCreateDto.builder()
                .name("Memchik")
                .email("dumala@sova.com")
                .age(20)
                .build();

        updateDto = UserUpdateDto.builder()
                .name("Memchik Dva")
                .email("dumala.dva@sova.com")
                .age(20)
                .build();
    }


    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("should create user successfully")
        void shouldCreateUser() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userMapper.toEntity(createDto)).thenReturn(user);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(responseDto);

            UserResponseDto result = userService.create(createDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("dumala@sova.com");

            verify(userRepository).existsByEmail("dumala@sova.com");
            verify(userRepository).save(user);
            verify(userMapper).toEntity(createDto);
            verify(userMapper).toResponseDto(user);
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when email exists")
        void shouldThrowWhenEmailExists() {
            when(userRepository.existsByEmail("dumala@sova.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.create(createDto))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("dumala@sova.com");

            verify(userRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("getById()")
    class GetByIdTests {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userMapper.toResponseDto(user)).thenReturn(responseDto);

            UserResponseDto result = userService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);

            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when not found")
        void shouldThrowWhenNotFound() {

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userMapper, never()).toResponseDto(any());
        }
    }


    @Nested
    @DisplayName("getAll()")
    class GetAllTests {

        @Test
        @DisplayName("should return list of users")
        void shouldReturnAllUsers() {

            User secondUser = User.builder()
                    .id(2L).name("Sova").email("nedumala@sova.com").age(21)
                    .createdAt(LocalDateTime.now()).build();

            UserResponseDto secondDto = UserResponseDto.builder()
                    .id(2L).name("Sova").email("nedumala@sova.com").age(21)
                    .createdAt(secondUser.getCreatedAt()).build();

            when(userRepository.findAll()).thenReturn(List.of(user, secondUser));
            when(userMapper.toResponseDtoList(List.of(user, secondUser)))
                    .thenReturn(List.of(responseDto, secondDto));

            List<UserResponseDto> result = userService.getAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(UserResponseDto::getEmail)
                    .containsExactly("dumala@sova.com", "nedumala@sova.com");

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no users")
        void shouldReturnEmptyList() {

            when(userRepository.findAll()).thenReturn(List.of());
            when(userMapper.toResponseDtoList(List.of())).thenReturn(List.of());

            List<UserResponseDto> result = userService.getAll();

            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should update user successfully")
        void shouldUpdateUser() {

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("dumala.dva@sova.com")).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);

            UserResponseDto updatedDto = UserResponseDto.builder()
                    .id(1L).name("Memchik Dva").email("dumala.dva@sova.com").age(30)
                    .createdAt(user.getCreatedAt()).build();
            when(userMapper.toResponseDto(user)).thenReturn(updatedDto);

            UserResponseDto result = userService.update(1L, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("dumala.dva@sova.com");

            verify(userMapper).updateEntity(user, updateDto);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when updating non-existent user")
        void shouldThrowWhenUserNotFound() {

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(99L, updateDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when new email is taken")
        void shouldThrowWhenNewEmailTaken() {

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("dumala.dva@sova.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.update(1L, updateDto))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("dumala.dva@sova.com");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should allow keeping the same email")
        void shouldAllowSameEmail() {

            UserUpdateDto sameEmailDto = UserUpdateDto.builder()
                    .name("Memchik Tut")
                    .email("dumala@sova.com")  // тот же email
                    .age(52)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(responseDto);

            UserResponseDto result = userService.update(1L, sameEmailDto);

            assertThat(result).isNotNull();
            verify(userRepository, never()).existsByEmail(anyString());
        }
    }


    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should delete user successfully")
        void shouldDeleteUser() {

            when(userRepository.existsById(1L)).thenReturn(true);

            userService.delete(1L);

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user not found")
        void shouldThrowWhenNotFound() {

            when(userRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> userService.delete(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userRepository, never()).deleteById(anyLong());
        }
    }


    @Nested
    @DisplayName("findByName()")
    class FindByNameTests {

        @Test
        @DisplayName("should return users matching name")
        void shouldReturnUsersByName() {

            when(userRepository.findByNameContainingIgnoreCase("Mem"))
                    .thenReturn(List.of(user));
            when(userMapper.toResponseDtoList(List.of(user)))
                    .thenReturn(List.of(responseDto));

            List<UserResponseDto> result = userService.findByName("Mem");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Memchik");

            verify(userRepository).findByNameContainingIgnoreCase("Mem");
        }

        @Test
        @DisplayName("should return empty list for blank name")
        void shouldReturnEmptyForBlankName() {

            List<UserResponseDto> result = userService.findByName("   ");

            assertThat(result).isEmpty();
            verify(userRepository, never()).findByNameContainingIgnoreCase(anyString());
        }

        @Test
        @DisplayName("should return empty list for null name")
        void shouldReturnEmptyForNullName() {

            List<UserResponseDto> result = userService.findByName(null);

            assertThat(result).isEmpty();
            verify(userRepository, never()).findByNameContainingIgnoreCase(anyString());
        }
    }

}
