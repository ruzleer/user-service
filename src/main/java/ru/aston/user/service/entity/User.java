package ru.aston.user.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // данные хранятся в таблице users
//@Check(constraints = "age > 0 AND length(name) > 0 AND length(email) > 0 "
//             + "AND email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$' ")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1)
    @Max(value = 150)
    private int age;

    @NotBlank
    @Pattern(
            regexp = "^[^0-9]+$",
            message = "Имя не должно содержать цифр"
    )
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public User(int age, String name, String email, LocalDateTime createdAt){
        this.age = age;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
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
