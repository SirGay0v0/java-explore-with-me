package ru.practicum.service.events.publ;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.State;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.config.Constants.formatter;

@Service
public class PublicEventServiceImpl implements PublicEventService {

    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;
    private final ModelMapper mapper;
    private final StatsClient client;
    private final String statsUri;


    public PublicEventServiceImpl(EventStorage eventStorage,
                                  RequestStorage requestStorage, ModelMapper mapper,
                                  StatsClient client, @Value("${stats-uri}") String statsUri) {
        this.eventStorage = eventStorage;
        this.requestStorage = requestStorage;
        this.mapper = mapper;
        this.client = client;
        this.statsUri = statsUri;
    }

    @Override
    public List<EventDtoResponse> getPublicEvents(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                                  String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                                  HttpServletRequest request) throws ValidationException {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;
        if (text == null) {
            text = "";
        }
        if (categoriesId == null) {
            categoriesId = new ArrayList<>();
        }

        if (rangeStart == null && rangeEnd == null) {
            events = eventStorage.findByAllCriteriaWithoutTime(text, categoriesId, paid, pageable);
            sendStatistic(request, "/events");
            return events.stream()
                    .map(event -> mapper.map(event, EventDtoResponse.class))
                    .collect(Collectors.toList());
        }
        LocalDateTime fromDate = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime toDate = LocalDateTime.parse(rangeEnd, formatter);
        if (toDate.isBefore(fromDate)) {
            throw new ValidationException("Start couldn't be after end");
        }
        events = eventStorage.findByAllCriteria(text, categoriesId, paid, fromDate, toDate, pageable);

        sendStatisticWithManyEvents(request, events);
        sendStatistic(request, "/events");

        return events.stream()
                .map(event -> mapper.map(event, EventDtoResponse.class))
                .map(event -> event.setConfirmedRequests(requestStorage.countConfirmedRequests(event.getId())))
                .map(this::getStat)
                .sorted(Comparator.comparing(EventDtoResponse::getViews))
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse getPublicEvent(Long id, HttpServletRequest request) throws NotFoundException, EntityNotFoundException {
        Event event = eventStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event not published");
        }

        EventDtoResponse eventDtoResponse = mapper.map(event, EventDtoResponse.class);
        eventDtoResponse.setConfirmedRequests(requestStorage.countConfirmedRequests(eventDtoResponse.getId()));
        String uri = statsUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + id;
        sendStatistic(request, "/events/" + event.getId());
        List<ViewStats> stats = getStat(uri);
        List<Event> events = new ArrayList<>();
        events.add(event);
        for (ViewStats views : stats) {
            eventDtoResponse.setViews(views.getHits());
        }
        return eventDtoResponse;
    }

    private void sendStatistic(HttpServletRequest request, String uri) {
        EndpointHitRequestDto endpointHitDto = new EndpointHitRequestDto();
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(uri);
        endpointHitDto.setTimestamp(LocalDateTime.now().format(formatter));
        String sendUri = statsUri + "/hit";
        client.postRequest(sendUri, endpointHitDto);
    }

    private void sendStatisticWithManyEvents(HttpServletRequest request, List<Event> events) {
        for (Event event : events) {
            String eventUri = "/events/" + event.getId();
            sendStatistic(request, eventUri);
        }
    }

    @SneakyThrows
    private List<ViewStats> getStat(String uri) {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<Object> response = client.getRequest(uri);
        Object body = response.getBody();
        String json = objectMapper.writeValueAsString(body);

        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));
    }

    private EventDtoResponse getStat(EventDtoResponse eventDtoResponse) {
        String uri = statsUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + eventDtoResponse.getId();
        List<ViewStats> list = getStat(uri);
        for (ViewStats stats : list) {
            eventDtoResponse.setViews(stats.getHits());
        }
        return eventDtoResponse;
    }
}
