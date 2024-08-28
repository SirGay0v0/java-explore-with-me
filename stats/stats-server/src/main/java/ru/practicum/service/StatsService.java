package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.InvalidDateTimeException;

import java.util.List;

public interface StatsService {
    String createHit(HttpServletRequest request, EndpointHitRequestDto endpointHit) throws InvalidDateTimeException;

    List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique) throws InvalidDateTimeException;
}
