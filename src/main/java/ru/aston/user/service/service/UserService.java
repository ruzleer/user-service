package ru.aston.user.service.service;

import ru.aston.user.service.dto.UserCreateDto;
import ru.aston.user.service.dto.UserResponseDto;
import ru.aston.user.service.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserCreateDto dto);
    UserResponseDto getById(Long id);
    List<UserResponseDto> getAll();
    UserResponseDto update(Long id, UserUpdateDto dto);
    void delete(Long id);
    List<UserResponseDto> findByName(String name);
}
