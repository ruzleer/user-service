package ru.aston.user.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {

    private String name;

    @Email
    private String email;

    @Min(value = 1)
    @Max(value = 150)
    private Integer age;
}