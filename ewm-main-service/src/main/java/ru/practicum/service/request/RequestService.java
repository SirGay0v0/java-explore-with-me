package ru.practicum.service.request;

import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestsByRequesterId(Long requesterId) throws NotFoundException;

    ParticipationRequestDto createRequest(Long userId, Long eventId) throws RequestErrorException, ParticipationsLimitOvercomeException;

    ParticipationRequestDto updateRequest(Long userId, Long requestId);
}
