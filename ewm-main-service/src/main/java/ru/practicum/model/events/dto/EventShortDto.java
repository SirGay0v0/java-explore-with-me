package ru.practicum.model.events.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.dto.UserDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class EventShortDto {
    String annotation;
    Category category;
    Integer confirmedRequests;
    String eventDate;
    Long id;
    UserDto initiator;
    Boolean paid;
    String title;
    Long views;
}
