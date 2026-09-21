package ru.aston.user.service.repository;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.user.service.controller.UserController;
import ru.aston.user.service.dto.UserCreateDto;
import ru.aston.user.service.dto.UserResponseDto;
import ru.aston.user.service.dto.UserUpdateDto;
import ru.aston.user.service.exception.UserNotFoundException;
import ru.aston.user.service.service.UserService;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    private UserResponseDto responseDto;
    private UserCreateDto createDto;

    @BeforeEach
    void setUp() {
        responseDto = UserResponseDto.builder()
                .id(1L)
                .name("Alex")
                .email("a@mail.ru")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        createDto = UserCreateDto.builder()
                .name("Alex")
                .email("a@mail.ru")
                .age(25)
                .build();
    }

    @Test
    void testGetUserByIdCode200() throws Exception {
        when(service.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("a@mail.ru"))
                .andExpect(jsonPath("$.age").value(25));

        verify(service).getById(1L);
    }

    @Test
    void testGetUserByIdCode404() throws Exception {
        when(service.getById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetUserByIdCode400() throws Exception {
        mockMvc.perform(get("/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserCode201AndBody() throws Exception {
        when(service.create(any(UserCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"));

        verify(service).create(any(UserCreateDto.class));
    }

    @Test
    void testCreateUserValidCode400() throws Exception {
        UserCreateDto invalid = UserCreateDto.builder()
                .name("")
                .email("bad-email")
                .age(-5)
                .build();

        mockMvc.perform(post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.age").exists());

        verifyNoInteractions(service);
    }

    @Test
    void testCreateUserCode400() throws Exception {
        mockMvc.perform(post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alex\",\"age\":\"not-a-number\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUsersCode200AndList() throws Exception {
        when(service.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testDeleteUserCode200() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/delete/1"))
                .andExpect(status().isOk());

        verify(service).delete(1L);
    }

    @Test
    void testGetUserByNameCode200andList() throws Exception {
        when(service.findByName("Alex")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/name/Alex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alex"));
    }

    @Test
    void testUpdateUserCode200() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(1L)
                .name("Gum")
                .email("new@mail.ru")
                .age(30)
                .build();

        when(service.update(eq(1L), any(UserUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).update(eq(1L), any(UserUpdateDto.class));
    }

    @Test
    void testUpdateUserCode400() throws Exception {
        UserUpdateDto invalid = UserUpdateDto.builder()
                .id(1L)
                .name("")
                .age(200)
                .build();

        mockMvc.perform(post("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
