package ru.aston.user.service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank
    @NotNull
    @Pattern(
            regexp = "^[^0-9]+$",
            message = "Имя не должно содержать цифр"
    )
    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Min(value = 1)
    @Max(value = 150)
    private Integer age;
}
