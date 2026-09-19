package ru.aston.user.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.user.service.dto.UserCreateDto;
import ru.aston.user.service.dto.UserResponseDto;
import ru.aston.user.service.dto.UserUpdateDto;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.*;
import ru.aston.user.service.mapper.UserMapper;
import ru.aston.user.service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        log.info("Creating user with email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            log.warn("Attempt to create user with existing email: {}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);

        log.info("User created successfully with id: {}", saved.getId());
        return userMapper.toResponseDto(saved);
    }

    @Override
    public UserResponseDto getById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return userMapper.toResponseDtoList(users);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().trim().equalsIgnoreCase(user.getEmail())) {

            String newEmail = dto.getEmail().trim().toLowerCase();
            if (userRepository.existsByEmail(newEmail)) {
                log.warn("Attempt to update user with existing email: {}", newEmail);
                throw new EmailAlreadyExistsException(newEmail);
            }
        }

        userMapper.updateEntity(user, dto);
        User updated = userRepository.save(user);

        log.info("User updated successfully with id: {}", updated.getId());
        return userMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public List<UserResponseDto> findByName(String name) {
        log.debug("Finding users by name: {}", name);

        if (name == null || name.isBlank()) {
            return List.of();
        }

        List<User> users = userRepository.findByNameContainingIgnoreCase(name.trim());
        log.debug("Found {} users with name like '{}'", users.size(), name);
        return userMapper.toResponseDtoList(users);
    }
}
