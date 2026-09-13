package ru.aston.user.service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // данные хранятся в таблице users
@Check(constraints = "age > 0 AND length(name) > 0 AND length(email) > 0 "
             + "AND email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$' ")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int age;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {
    }
    public User(int age, String name, String email, LocalDateTime createdAt){
        this.age = age;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email=" + email +
                ", created_at=" + createdAt +
                '}';
    }
}
