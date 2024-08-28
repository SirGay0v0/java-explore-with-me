package ru.practicum.model.compilations.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.events.dto.EventShortDtoDb;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class CompilationResponseDto {
    Long id;
    String title;
    Boolean pinned;
    List<EventShortDtoDb> events;
}
