package ru.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.InvalidDateTimeException;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public String createHit(HttpServletRequest request, @RequestBody EndpointHitRequestDto endpointRequestHit) throws JsonProcessingException, InvalidDateTimeException {
        String response = service.createHit(request, endpointRequestHit);
        return mapper.writeValueAsString(response);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStat(@RequestParam(defaultValue = "2021-01-01 00:00:00") String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false", required = false) Boolean unique) throws InvalidDateTimeException {
        return service.getStat(start, end, uris, unique);
    }
}
