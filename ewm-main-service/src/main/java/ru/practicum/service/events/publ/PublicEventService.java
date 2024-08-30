package ru.practicum.service.events.publ;


import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.events.dto.EventDtoResponse;

import java.util.List;

public interface PublicEventService {
    List<EventDtoResponse> getPublicEvents(String textAnnotation, List<Long> categoriesId, Boolean paid, String rangeStart,
                                           String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest request) throws ValidationException;

    EventDtoResponse getPublicEvent(Long eventId, HttpServletRequest request) throws NotFoundException, EntityNotFoundException;
}
