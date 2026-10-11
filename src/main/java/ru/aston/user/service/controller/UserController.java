package ru.aston.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation
            (summary = "Получить одного пользователя",
             description = "Метод возвращает существующего пользователя и выводит его параметры")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "Идентификатор пользователя")
                                                       @PathVariable Long id) {
        UserResponseDto user = service.getById(id);

        user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).deleteUserById(id)).withRel("delete"));
        user.add(linkTo(methodOn(UserController.class).getAllUsersById()).withRel("users"));

        return ResponseEntity.ok(user);
    }

    @Operation
            (summary = "Создать пользователя",
             description = "Метод получает на вход данные и создает запись в БД со входными параметрами")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Parameter(description = "Входные параметры: имя, возраст, почта")
                                                      @Valid @RequestBody UserCreateDto dto) {
        UserResponseDto newUser = service.create(dto);

        newUser.add(linkTo(methodOn(UserController.class).getUserById(newUser.getId())).withSelfRel());
        newUser.add(linkTo(methodOn(UserController.class).getAllUsersById()).withRel("users"));

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation
            (summary = "Получить всех пользователей",
             description = "Метод возвращает всех существующих в БД пользователей")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsersById() {
        List<UserResponseDto> users = service.getAll();

        for (UserResponseDto dto : users) {
            dto.add(linkTo(methodOn(UserController.class).getUserById(dto.getId())).withSelfRel());
        }

        return ResponseEntity.ok(users);
    }

    @Operation
            (summary = "Удалить пользователя",
             description = "Метод выполняет удаление пользователя из БД с идентификатором, указанным в запросе")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@Parameter(description = "Идентификатор пользователя")
                                                     @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation
            (summary = "Получить пользователей по имени",
             description = "Метод возвращает всех существующих пользователей с именем, указанным в запросе ")
    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserResponseDto>> getUserByName(@Parameter(description = "Имя пользователя")
                                                               @PathVariable String name) {
        List<UserResponseDto> user = service.findByName(name);

        for (UserResponseDto dto : user) {
            dto.add(linkTo(methodOn(UserController.class).getUserByName(name)).withSelfRel());
        }

        return ResponseEntity.ok(user);
    }

    @Operation
            (summary = "Обновить данные пользователя",
             description = "Метод выполняет обновление данных существующего пользователя с идентификатором, указанным в теле запроса. Новые(изменяемые) данные также передаются в теле запроса: имя, возраст, почта")
    @PostMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "Входные параметры: идентификатор пользователя, имя, возраст, почта")
                                                      @Valid @RequestBody UserUpdateDto dto){
        UserResponseDto newUser = service.update(dto.getId(), dto);

        newUser.add(linkTo(methodOn(UserController.class).getUserById(dto.getId())).withSelfRel());
        newUser.add(linkTo(methodOn(UserController.class).deleteUserById(dto.getId())).withRel("delete"));

        return ResponseEntity.ok(newUser);
    }

}
