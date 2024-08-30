package ru.practicum.service.events.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventAlreadyPublishedException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.EventPublicationException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.events.AdminStateAction;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.State;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.model.events.dto.UpdateEventAdminRequest;
import ru.practicum.model.location.Location;
import ru.practicum.storage.CategoryStorage;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.Constants.formatter;

@Service
public class AdminEventServiceImpl implements AdminEventService {
    @Autowired
    private final EventStorage eventStorage;
    @Autowired
    private final CategoryStorage categoryStorage;
    @Autowired
    private final RequestStorage requestStorage;
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private final StatsClient client;

    private final String statsUri;

    public AdminEventServiceImpl(EventStorage eventStorage, CategoryStorage categoryStorage,
                                 RequestStorage requestStorage, ModelMapper mapper,
                                 StatsClient client, @Value("${stats-uri}") String statsUri) {
        this.eventStorage = eventStorage;
        this.categoryStorage = categoryStorage;
        this.requestStorage = requestStorage;
        this.mapper = mapper;
        this.client = client;
        this.statsUri = statsUri;
    }

    @Override
    public List<EventDtoResponse> getAdminEvents(List<Long> usersId, List<String> states, List<Long> categoriesId,
                                                 String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = new ArrayList<>();
        List<State> stateList = new ArrayList<>();

        if (usersId == null && states == null && categoriesId == null && rangeStart == null && rangeEnd == null) {
            events = eventStorage.findByEmptyParameters(pageable);
            return events.stream()
                    .map(event -> mapper.map(event, EventDtoResponse.class))
                    .map(event -> event.setConfirmedRequests(requestStorage.countConfirmedRequests(event.getId())))
                    .map(this::getStat)
                    .collect(Collectors.toList());
        }

        if (categoriesId == null) {
            categoriesId = new ArrayList<>();
        }
        if (usersId == null) {
            usersId = new ArrayList<>();
        }
        if (states != null) {
            for (String state : states) {
                stateList.add(State.valueOf(state));
            }
        } else {
            stateList = new ArrayList<>();
        }
        if (rangeStart == null && rangeEnd == null) {
            events = eventStorage.findEventByUsersAndStateAndCategory(usersId, stateList, categoriesId, pageable);
        }
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, formatter);
            events = eventStorage.findEventByUsersAndStateAndCategoryBetween(usersId, stateList, categoriesId,
                    startDateTime, endDateTime, pageable);
        }

        return events.stream()
                .map(event -> mapper.map(event, EventDtoResponse.class))
                .map(event -> event.setConfirmedRequests(requestStorage.countConfirmedRequests(event.getId())))
                .map(this::getStat)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse updateAdminEvent(Long eventId, UpdateEventAdminRequest updateRequest) throws NotFoundException,
            EventPublicationException, EventAlreadyPublishedException, EventPatchException, EntityNotFoundException {
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (updateRequest.getEventDate() != null) {
            LocalDateTime time = LocalDateTime.parse(updateRequest.getEventDate(), formatter);

            if (!time.isAfter(LocalDateTime.now().plusHours(1))) {
                throw new EventPatchException("Event with id " + eventId + " couldn't be less than 1 hours. Date: " + time);
            }
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventAlreadyPublishedException("Event with id " + eventId + " already published " + event.getState());
        }
        if (event.getState().equals(State.CANCELED)) {
            throw new EventPublicationException("Event with id eventId already published event.getState())");
        }

        this.updateAdminEvent(event, updateRequest);
        eventStorage.save(event);

        return mapper.map(event, EventDtoResponse.class);
    }

    @SneakyThrows
    private List<ViewStats> getStat(String uri) {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<Object> response = client.getRequest(uri);
        Object body = response.getBody();
        String json = mapper.writeValueAsString(body);

        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));
    }

    private EventDtoResponse getStat(EventDtoResponse eventDtoResponse) {
        String uri = statsUri + "/stats?end=2041-01-01 00:00:00&unique=true&uris=/events/" + eventDtoResponse.getId();
        List<ViewStats> list = getStat(uri);
        for (ViewStats stats : list) {
            eventDtoResponse.setViews(stats.getHits());
        }
        if (eventDtoResponse.getViews() == null) {
            eventDtoResponse.setViews(0L);
        }
        return eventDtoResponse;
    }

    private void updateAdminEvent(Event target, UpdateEventAdminRequest source) throws EntityNotFoundException {
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
                    new EntityNotFoundException("Category with id " + source.getCategory() + " was not found"));
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
        if (source.getStateAction() != null && source.getStateAction().equals(AdminStateAction.REJECT_EVENT.toString())) {
            target.setState(State.CANCELED);
        } else {
            target.setState(State.PUBLISHED);
            target.setPublishedOn(LocalDateTime.now());
        }
    }
}
