package ru.practicum.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.events.dto.EventDtoResponse;
import ru.practicum.service.events.publ.PublicEventServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicEventServiceImpl service;


    @GetMapping
    public List<EventDtoResponse> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) throws ValidationException {
        return service.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventDtoResponse getEvent(@PathVariable Long id,
                                     HttpServletRequest request) throws NotFoundException, EntityNotFoundException {
        return service.getPublicEvent(id, request);
    }
}
