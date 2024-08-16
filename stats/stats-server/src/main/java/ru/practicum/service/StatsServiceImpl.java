package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;
import ru.practicum.storage.HitStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitStorage storage;
    private final ModelMapper mapper;

    @Override
    public EndpointHitRequestDto saveHit(EndpointHit endpointHit) {
        return mapper.map(storage.save(endpointHit), EndpointHitRequestDto.class);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from = LocalDateTime.parse(start, formatter);
        LocalDateTime to = LocalDateTime.parse(end, formatter);


        if (unique) {
            return storage.getUniqueStats(from, to, uris);
        }
        return storage.getStats(from, to, uris);
    }
}
