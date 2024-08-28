package ru.practicum.service.events.admin;

import org.apache.coyote.BadRequestException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventAlreadyPublishedException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.EventPublicationException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.model.events.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventDtoResponse> getAdminEvents(List<Long> usersId, List<String> states, List<Long> categoriesId,
                                          String rangeStart, String rangeEnd, Integer from, Integer size);

    EventDtoResponse updateAdminEvent(Long eventId, UpdateEventAdminRequest updateRequest)
            throws BadRequestException, NotFoundException, EventPublicationException, EventAlreadyPublishedException, EventPatchException, EntityNotFoundException;
}
