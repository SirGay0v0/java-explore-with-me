package ru.practicum.model.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class UpdateCompilationDto {
    @Size(min = 1, max = 50)
    String title;
    List<Long> events;
    Boolean pinned;
}
