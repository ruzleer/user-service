package ru.aston.user.service.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotNull
    @Min(value = 1)
    @Max(value = 150)
    private Integer age;
}
