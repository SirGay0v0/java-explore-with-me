package ru.practicum.controller.priv;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
import ru.practicum.service.events.priv.PrivateEventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final PrivateEventService service;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoResponse addEvent(
            @PathVariable Long userId,
            @RequestBody @Valid EventDto eventDto) throws BadRequestException, EventPatchException {
        return service.addEvent(userId, eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDtoResponse updateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventUserRequest updateRequest)
            throws EventPatchException, EventAlreadyPublishedException, NotFoundException {
        return service.updateUserEvent(userId, eventId, updateRequest);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDtoResponse getFullEvent(@PathVariable Long userId,
                                         @PathVariable Long eventId) throws EntityNotFoundException {
        return service.getFullEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.getPrivateEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable(name = "userId") Long userId,
                                                     @PathVariable(name = "eventId") Long eventId) throws
            EntityNotFoundException {
        return service.getPrivateRequests(userId, eventId);
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody(required = false) EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws ParticipationsLimitOvercomeException, RequestErrorException, ru.practicum.exceptions.EntityNotFoundException {
        return service.updateStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
