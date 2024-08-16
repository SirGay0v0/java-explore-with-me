package ru.practicum.service;

import ru.practicum.EndpointHit;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatsService {

    EndpointHitRequestDto saveHit(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique);
}
