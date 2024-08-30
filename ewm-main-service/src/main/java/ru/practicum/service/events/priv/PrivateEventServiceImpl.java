package ru.practicum.service.events.priv;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.EventAlreadyPublishedException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.category.Category;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.PrivateStateAction;
import ru.practicum.model.events.State;
import ru.practicum.model.events.Status;
import ru.practicum.model.events.dto.EventDto;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.model.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.events.dto.EventShortDto;
import ru.practicum.model.events.dto.UpdateEventUserRequest;
import ru.practicum.model.location.Location;
import ru.practicum.model.requests.ParticipationRequest;
import ru.practicum.model.requests.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.storage.CategoryStorage;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;
import ru.practicum.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.Constants.formatter;

@Service
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventStorage eventStorage;
    private final CategoryStorage categoryStorage;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final ModelMapper mapper;
    private final StatsClient client;
    private final String statsUri;

    public PrivateEventServiceImpl(EventStorage eventStorage, UserStorage userStorage,
                                   CategoryStorage categoryStorage, RequestStorage requestStorage, ModelMapper mapper,
                                   @Value("${stats-uri}") String statsUri, StatsClient client) {
        this.eventStorage = eventStorage;
        this.categoryStorage = categoryStorage;
        this.requestStorage = requestStorage;
        this.userStorage = userStorage;
        this.mapper = mapper;
        this.client = client;
        this.statsUri = statsUri;
    }

    @Override
    public EventDtoResponse addEvent(Long userId, EventDto eventDto) throws EventPatchException {

        Category category = categoryStorage.findById(eventDto.getCategory()).orElseThrow(() -> new
                EntityNotFoundException("Category with id " + eventDto.getCategory() + " was not found"));
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        Event event = mapper.map(eventDto, Event.class);
        event.setInitiator(user);
        event.setCategory(category);
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventPatchException("Event should have date after the 2 hours " + event.getEventDate());
        }

        eventStorage.save(event);
        return mapper.map(event, EventDtoResponse.class);
    }

    @Override
    public List<EventShortDto> getPrivateEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));

        return eventStorage.findAllByInitiator(user, pageable).stream()
                .map(event -> event.setConfirmedRequests(requestStorage.countRequests(event.getId())))
                .map(event -> mapper.map(event, EventShortDto.class))
                .map(this::getStatForShortEvent)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse getFullEvent(Long userId, Long eventId) throws EntityNotFoundException {
        Event event = eventStorage.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id " + eventId + " was not found"));
        EventDtoResponse eventDtoResponse = mapper.map(event, EventDtoResponse.class);
        eventDtoResponse.setConfirmedRequests(requestStorage.countConfirmedRequests(eventId));
        getStat(eventDtoResponse);
        return eventDtoResponse;
    }

    @Override
    public EventDtoResponse updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest eventRequest)
            throws NotFoundException, EventAlreadyPublishedException, EventPatchException {

        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id " + eventId));

        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventAlreadyPublishedException("Event with id " + eventId + " already published " + event.getState() +
                    "and couldn't be changed");
        }

        if (eventRequest.getEventDate() != null) {
            LocalDateTime moment = LocalDateTime.parse(eventRequest.getEventDate(), formatter);
            if (!moment.isAfter(LocalDateTime.now().plusHours(2))) {
                throw new EventPatchException("Event with id " + eventId + "couldn't be less than 2 hours. Date: " + moment);
            }
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventPatchException("User with id " + userId + "couldn't patch user's event with id " + event.getInitiator().getId());
        }
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventPatchException("Event with id " + eventId + "couldn't be less than 2 hours. Date: " + event.getEventDate());
        }
        updatePublicEvent(event, eventRequest);
        eventStorage.save(event);
        EventDtoResponse eventDtoResponse = mapper.map(event, EventDtoResponse.class);
        eventDtoResponse.setConfirmedRequests(requestStorage.countConfirmedRequests(event.getId()));
        return eventDtoResponse;
    }


    @Override
    public List<ParticipationRequestDto> getPrivateRequests(Long userId, Long eventId) {
        eventStorage.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        List<ParticipationRequest> participationRequests = requestStorage.findAllByEventId(eventId);
        return participationRequests.stream()
                .map(req -> mapper.map(req, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws EntityNotFoundException, ParticipationsLimitOvercomeException, RequestErrorException {
        try {
            Event event = eventStorage.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                    " was not found"));
            List<Long> listIdsRequests = eventRequestStatusUpdateRequest.getRequestIds();

            if (event.getParticipantLimit() == 0) {
                for (Long id : listIdsRequests) {
                    ParticipationRequest request = requestStorage.findById(id).orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                            " was not found"));
                    request.setStatus(Status.CONFIRMED);
                    requestStorage.save(request);
                }
            }

            String status = eventRequestStatusUpdateRequest.getStatus();
            return fillStatus(event, listIdsRequests, status);
        } catch (NullPointerException | EntityNotFoundException e) {
            throw new RequestErrorException("Event with id " + eventId + " was already accepted");
        }
    }

    private EventShortDto getStatForShortEvent(EventShortDto shortDto) {
        String uri = statsUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + shortDto.getId();
        List<ViewStats> list = getStat(uri);
        for (ViewStats stats : list) {
            shortDto.setViews(stats.getHits());
        }
        if (shortDto.getViews() == null) {
            shortDto.setViews(0L);
        }
        return shortDto;
    }

    @SneakyThrows
    private List<ViewStats> getStat(String uri) {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<Object> response = client.getRequest(uri);
        Object body = response.getBody();
        String json = mapper.writeValueAsString(body);

        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));
    }

    private void getStat(EventDtoResponse eventDtoResponse) {
        String uri = statsUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + eventDtoResponse.getId();
        List<ViewStats> list = this.getStat(uri);
        for (ViewStats stats : list) {
            eventDtoResponse.setViews(stats.getHits());
        }
        if (eventDtoResponse.getViews() == null) {
            eventDtoResponse.setViews(0L);
        }
    }

    private void updatePublicEvent(Event target, UpdateEventUserRequest source) throws EntityNotFoundException {
        if (source.getAnnotation() != null) {
            target.setAnnotation(source.getAnnotation());
        }
        if (source.getEventDate() != null) {
            target.setEventDate(LocalDateTime.parse(source.getEventDate(), formatter));
        }
        if (source.getPaid() != null) {
            target.setPaid(source.getPaid());
        }
        if (source.getLocation() != null) {
            target.setLocation(mapper.map(source.getLocation(), Location.class));
        }
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        if (source.getCategory() != null) {
            Category category = categoryStorage.findById(source.getCategory()).orElseThrow(() ->
                    new EntityNotFoundException("Category with id " + source.getCategory() +
                            " was not found"));
            target.setCategory(category);
        }
        if (source.getTitle() != null) {
            target.setTitle(source.getTitle());
        }
        if (source.getRequestModeration() != null) {
            target.setRequestModeration(source.getRequestModeration());
        }
        if (source.getParticipantLimit() != null) {
            target.setParticipantLimit(source.getParticipantLimit());
        }
        if (source.getStateAction() != null && source.getStateAction().equals(PrivateStateAction.CANCEL_REVIEW.toString())) {
            target.setState(State.CANCELED);
        } else {
            target.setState(State.PENDING);
            target.setPublishedOn(LocalDateTime.now());
        }
    }

    private EventRequestStatusUpdateResult fillStatus(Event event, List<Long> requestIds, String status)
            throws EntityNotFoundException, ParticipationsLimitOvercomeException {
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        updateResult.setConfirmedRequests(new ArrayList<>());
        updateResult.setRejectedRequests(new ArrayList<>());
        Status statusEnum = Status.valueOf(status);
        switch (statusEnum) {
            case CONFIRMED:
                for (Long id : requestIds) {
                    Long count = requestStorage.countRequests(event.getId());
                    ParticipationRequest request = requestStorage.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                                    " was not found"));
                    if (count < event.getParticipantLimit()) {
                        request.setStatus(Status.CONFIRMED);
                        requestStorage.save(request);
                        updateResult.getConfirmedRequests().add(mapper.map(request, ParticipationRequestDto.class));
                    } else {
                        throw new ParticipationsLimitOvercomeException("limit is overcome");
                    }
                }
                break;
            case REJECTED:
                for (Long id : requestIds) {
                    ParticipationRequest request = requestStorage.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                                    " was not found"));
                    request.setStatus(Status.REJECTED);
                    requestStorage.save(request);
                    updateResult.getRejectedRequests().add(mapper.map(request, ParticipationRequestDto.class));
                }
                break;
        }
        return updateResult;
    }
}
