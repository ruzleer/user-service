package ru.aston.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.user.service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByNameContainingIgnoreCase(String name);
}