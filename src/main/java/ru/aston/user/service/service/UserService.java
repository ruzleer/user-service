package ru.aston.user.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.dao.UserDaoImpl;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.EmailCheckException;
import ru.aston.user.service.exception.UserNotFoundException;
import ru.aston.user.service.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public User createUser(String name, String email, int age) {
        logger.info("Запрос на создание нового пользователя: name={}, email={}, age={}", name, email, age);
        checkEnteredData(name, email, age);
        if (userDao.existByEmail(email)) {
            logger.warn("Попытка создания пользователя с уже существующим email: {}", email);
            throw new EmailCheckException("Email already exists: " + email);
        }
        logger.debug("Email {} свободен, создание пользователя разрешено", email);
        logger.debug("Создание объекта User");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        logger.debug("Объект User создан: name={}, email={}, age={}", user.getName(), user.getEmail(), user.getAge());
        return userDao.save(user);
    }

    public void deleteUser(Long id) {
        logger.info("Запрос на удаление пользователя: id={}", id);
        if (id == null || id <= 0) {
            logger.warn("Попытка не правильного ввода id");
            throw new ValidationException("Invalid user ID");
        }
        userDao.delete(id);
    }

    public User updateUser(Long id, String name, String email, int age) {
        logger.info("Запрос на обновление пользователя: id={}", id);
        Optional<User> existingUserOpt = userDao.findById(id);
        if (existingUserOpt.isEmpty()) {
            logger.warn("Пользователь с id={} не найден", id);
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        User existingUser = existingUserOpt.get();

        checkEnteredData(name, email, age);
        if (userDao.existByEmail(email) && !existingUser.getEmail().equals(email)) {
            logger.warn("Попытка обновление уже существующим email: {}", email);
            throw new EmailCheckException("Email already exists: " + email);
        }

        existingUser.setName(name.trim());
        existingUser.setEmail(email.trim());
        existingUser.setAge(age);
        logger.debug("Объект User обновлен: name={}, email={}, age={}", existingUser.getName(), existingUser.getEmail(), existingUser.getAge());
        return userDao.update(existingUser);
    }

    public Optional<User> findById(Long id) {
        logger.info("Запрос на поиск пользователя: id={}", id);
        if (id == null || id <= 0) {
            logger.warn("Попытка не правильного ввода id");
            throw new ValidationException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        logger.info("Запрос на поиск пользователей");
        return userDao.findAll();
    }

    private void checkEnteredData(String name, String email, int age) {
        logger.debug("Начало валидации данных пользователя");
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Попытка ввести пустую строку вместо имени");
            throw new ValidationException("Name cannot be empty");
        }
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            logger.warn("Попытка ввести не правильный формат почты");
            throw new ValidationException("Invalid email format");
        }
        if (age <= 0) {
            logger.warn("Попытка ввести отрицательный возраст");
            throw new ValidationException("Age must be positive");
        }
        logger.debug("Валидация данных успешно пройдена");
    }
}
