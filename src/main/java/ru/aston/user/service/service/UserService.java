package ru.aston.user.service.service;

import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.dao.UserDaoImpl;
import ru.aston.user.service.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public User createUser(String name, String email, int age) {
        checkEnteredData(name, email, age);
        if (userDao.existByEmail(email)) {
            throw new IllegalStateException("Email already exists: " + email);
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreatedAt(LocalDateTime.now());
        return userDao.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userDao.delete(id);
    }

    public User updateUser(Long id, String name, String email, int age) {
        Optional<User> existingUserOpt = userDao.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new IllegalStateException("User not found with ID: " + id);
        }

        User existingUser = existingUserOpt.get();

        checkEnteredData(name, email, age);
        if (userDao.existByEmail(email) && !existingUser.getEmail().equals(email)) {
            throw new IllegalStateException("Email already exists: " + email);
        }

        existingUser.setName(name.trim());
        existingUser.setEmail(email.trim());
        existingUser.setAge(age);
        existingUser.setCreatedAt(LocalDateTime.now());
        return userDao.update(existingUser);
    }

    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    private void checkEnteredData(String name, String email, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
    }
}
