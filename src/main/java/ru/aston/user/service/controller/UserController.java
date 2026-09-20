package ru.aston.user.service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.user.service.dto.UserCreateDto;
import ru.aston.user.service.dto.UserResponseDto;
import ru.aston.user.service.dto.UserUpdateDto;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.service.UserService;

import java.util.List;

@RestController
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = service.getById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        UserResponseDto newUser = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsersById() {
        List<UserResponseDto> users = service.getAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserResponseDto>> getUserByName(@PathVariable String name) {
        List<UserResponseDto> user = service.findByName(name);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateDto dto){
        UserResponseDto newUser = service.update(dto.getId(), dto);
        return ResponseEntity.ok(newUser);
    }

}
