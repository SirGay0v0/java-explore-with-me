package ru.practicum.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class NewUserRequest {
    @NotNull
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
    @NotNull
    @NotBlank
    @Length(min = 6, max = 64)
    String email;
}
