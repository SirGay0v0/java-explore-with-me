package ru.practicum.model.events.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.model.user.User;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class EventDtoResponse {
    String annotation;
    Category category;
    Long confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    Long id;
    User initiator;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Long views;
}
