package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.State;
import ru.practicum.model.events.Status;
import ru.practicum.model.requests.ParticipationRequest;
import ru.practicum.model.requests.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;
import ru.practicum.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestStorage requestStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final ModelMapper mapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByRequesterId(Long requesterId) throws NotFoundException {

        if (!userStorage.existsById(requesterId)) {
            throw new NotFoundException("User not found with id " + requesterId);
        }

        List<ParticipationRequest> participationRequests = requestStorage.findAllByRequesterId(requesterId);

        return participationRequests.stream()
                .map(req -> mapper.map(req, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) throws RequestErrorException, ParticipationsLimitOvercomeException {
        User requester = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        ParticipationRequest request = new ParticipationRequest();
        ParticipationRequest lastRequest = requestStorage.findByRequesterId(userId);

        if (lastRequest != null) {
            throw new RequestErrorException("request couldn't be created as was made before");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new RequestErrorException("request couldn't be created on unpublished event");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RequestErrorException("request couldn't be created by author event");
        }

        request.setRequester(requester);
        request.setEvent(event);
        request.setStatus(Status.PENDING);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        }
        long confirmedRequest = requestStorage.countRequests(event.getId());
        if (event.getParticipantLimit() <= confirmedRequest && event.getParticipantLimit() != 0) {
            throw new ParticipationsLimitOvercomeException("limit request was reached " + confirmedRequest);
        }
        request = requestStorage.save(request);
        return mapper.map(request, ParticipationRequestDto.class);
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestStorage.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to cancel this request");
        } else {
            request.setStatus(Status.CANCELED);
        }
        return mapper.map(requestStorage.save(request), ParticipationRequestDto.class);
    }
}
