package ru.practicum.service.events.priv;

import org.apache.coyote.BadRequestException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventAlreadyPublishedException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.events.dto.EventDto;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.model.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.events.dto.EventShortDto;
import ru.practicum.model.events.dto.UpdateEventUserRequest;
import ru.practicum.model.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    EventDtoResponse addEvent(Long userId, EventDto eventDto) throws BadRequestException, EventPatchException;

    List<EventShortDto> getPrivateEvents(Long userId, int from, int size);

    EventDtoResponse getFullEvent(Long userId, Long eventId);

    EventDtoResponse updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest)
            throws NotFoundException, EventAlreadyPublishedException, EventPatchException;

    List<ParticipationRequestDto> getPrivateRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws ParticipationsLimitOvercomeException, EntityNotFoundException, RequestErrorException;
}
