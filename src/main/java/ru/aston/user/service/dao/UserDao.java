package ru.aston.user.service.dao;

import ru.aston.user.service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    User update(User user);
    void delete(Long id);
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean existByEmail(String email);
}
